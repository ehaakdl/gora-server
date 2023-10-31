package org.gora.server.component.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.gora.server.common.CommonUtils;
import org.gora.server.common.Env;
import org.gora.server.common.NetworkUtils;
import org.gora.server.model.ClientConnection;
import org.gora.server.model.network.ClientNetworkBuffer;
import org.gora.server.model.network.ClientResource;
import org.gora.server.model.network.NetworkPakcetProtoBuf;
import org.gora.server.model.network.NetworkPakcetProtoBuf.NetworkPacket;
import org.gora.server.model.network.eNetworkType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@Getter
public class ClientManager {
    @Value("${app.udp_client_port}")
    private int udpClientPort;
    private UdpServer udpServer;

    // key는 채널 아이디(네티는 전체 채널 인스턴스에 대한 고유한 아이디를 가지고있다.)
    private final static Map<String, ClientResource> resources = new ConcurrentHashMap<>(
            Integer.parseInt(System.getenv(Env.MAX_DEFAULT_QUE_SZ)));

    // 패킷 수신핸들러가 아닌 다른 진입점에서 채널 아이디를 가져오기 위한 저장소
    // key 는 userSeq
    private final static Map<Long, String> userResourceMap = new ConcurrentHashMap<>(
            Integer.parseInt(System.getenv(Env.MAX_DEFAULT_QUE_SZ)));

    public boolean existsResource(String resourceKey){
        return resources.containsKey(resourceKey);
    }
    public void createResource(String resourceKey, ClientConnection connection) {

        ClientNetworkBuffer buffer = ClientNetworkBuffer.builder()
                .tcpRecvBuffer(new ByteArrayOutputStream())
                .udpRecvBuffer(new ByteArrayOutputStream())
                .build();
        
        resources.putIfAbsent(resourceKey, ClientResource.builder().buffer(buffer).connection(connection).build());        
    }

    // 사이즈에 맞다면 NetworkPacket 클래스 반환
    public List<NetworkPakcetProtoBuf.NetworkPacket> assemblePacket(String resourceKey, eNetworkType type,
            byte[] packetBytes)
            throws IOException, ClassNotFoundException {
        ClientResource resource = resources.get(resourceKey);
        if (resource == null) {
            throw new RuntimeException();
        }

        ClientNetworkBuffer buffer = resource.getBuffer();
        if (buffer == null) {
            buffer = ClientNetworkBuffer.builder()
                    .tcpRecvBuffer(new ByteArrayOutputStream())
                    .udpRecvBuffer(new ByteArrayOutputStream())
                    .build();
            resource.setBuffer(buffer);
        }

        ByteArrayOutputStream recvBuffer;
        
        if (type == eNetworkType.tcp) {
            recvBuffer = buffer.getTcpRecvBuffer();
        } else {
            recvBuffer = buffer.getUdpRecvBuffer();
        }

        List<NetworkPacket> result = new ArrayList<>();
        recvBuffer.write(packetBytes);
        int assembleTotalCount = 0;
        if (recvBuffer.size() >= NetworkUtils.TOTAL_MAX_SIZE) {
            int remainRecvByte = recvBuffer.size() % NetworkUtils.TOTAL_MAX_SIZE;
            assembleTotalCount = recvBuffer.size() / NetworkUtils.TOTAL_MAX_SIZE;
            
            // 네트워크 패킷 클래스로 역직렬화
            byte[] convertBytes = new byte[NetworkUtils.TOTAL_MAX_SIZE];
            int from = 0;
            int to;
            for (int count = 0; count < assembleTotalCount; count++) {   
                from = count * NetworkUtils.TOTAL_MAX_SIZE;
                to = (count+1) * NetworkUtils.TOTAL_MAX_SIZE;
                 
                convertBytes = Arrays.copyOfRange(recvBuffer.toByteArray(), from, to);
                result.add((NetworkPacket) CommonUtils.bytesToObject(convertBytes));
            }
            
            // 역직렬화 후 남은 데이터는 추출해서 buffer reset후 다시 buffer에 추가
            if(remainRecvByte > 0){
                int endPos = recvBuffer.toByteArray().length - remainRecvByte;
                byte[] remainBytes = new byte[remainRecvByte];
                System.arraycopy(recvBuffer.toByteArray(), endPos, remainBytes, 0, remainBytes.length);
                recvBuffer.reset();
                recvBuffer.write(remainBytes);
            }else{
                recvBuffer.reset();
            }
            
            return result;
        } else {
            return null;
        }
    }

    public boolean send(NetworkPacket data) {
        // ClientConnection clientConnection = clients.get(data.getKey());
        // if (clientConnection == null) {
        // log.error("클라이언트 존재 안함");
        // return false;
        // }

        // ByteBuf sendBuf = NetworkPacket.convertByteBuf(data, objectMapper);
        // if (sendBuf == null) {
        // log.error("송신 데이터 파싱 실패");
        // return false;
        // }

        // if (data.getProtocol() == eProtocol.tcp) {
        // if (!clientConnection.isConnectionTcp()) {
        // log.error("클라이언트와 TCP 연결 안됨");
        // return false;
        // }

        // clientConnection.getTcpChannel().writeAndFlush(sendBuf).addListener(future ->
        // {
        // if (!future.isSuccess()) {
        // log.error("송신 실패");
        // log.error(CommonUtils.getStackTraceElements(future.cause()));
        // }
        // });
        // } else {
        // udpServer.send(clientConnection.getClientIp(), udpClientPort,
        // sendBuf.array());
        // }

        return true;
    }

    public void close(String resourceKey, Long userSeq) {
        ClientResource clientResource;
        if(resourceKey != null){
            clientResource  = resources.get(resourceKey);
        }else if(userSeq != null){
            resourceKey = userResourceMap.get(userSeq);
            if(resourceKey == null){
                return;
            }
            clientResource  = resources.get(resourceKey);
        }else{
            throw new RuntimeException();
        }

        if (clientResource == null) {
                return;
        }
        resources.remove(resourceKey);

        ClientNetworkBuffer buffer = clientResource.getBuffer();
        if(buffer != null){
            buffer.getTcpRecvBuffer().reset();
            buffer.getUdpRecvBuffer().reset();
        }

        ClientConnection clientConnection = clientResource.getConnection();
        if (clientConnection != null) {
            if (clientConnection.isConnectionTcp()) {
                clientConnection.getTcpChannel().close();
            }
        }
    }
	public int getClientCount() {
        return resources.size();
	}
}
