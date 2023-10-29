package org.gora.server.component.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
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
import org.gora.server.model.network.eNetworkType;
import org.gora.server.model.network.NetworkPakcetProtoBuf.NetworkPacket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties.RSocket.Client;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.channel.Channel;
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
    private Map<String, ClientResource> resources = new ConcurrentHashMap<>(
            Integer.parseInt(System.getenv(Env.MAX_DEFAULT_QUE_SZ)));

    // 패킷 수신핸들러가 아닌 다른 진입점에서 채널 아이디를 가져오기 위한 저장소
    // key 는 userSeq
    private final static Map<Long, String> userResourceMap = new ConcurrentHashMap<>(
            Integer.parseInt(System.getenv(Env.MAX_DEFAULT_QUE_SZ)));

    public boolean existsResource(String resourceKey){
        return this.resources.containsKey(resourceKey);
    }
    public void createResource(String resourceKey, ClientConnection connection) {

        ClientNetworkBuffer buffer = ClientNetworkBuffer.builder()
                .tcpBuffer(new ByteArrayOutputStream())
                .udpBuffer(new ByteArrayOutputStream())
                .build();
        
        this.resources.putIfAbsent(resourceKey, ClientResource.builder()
                .buffer(buffer).connection(connection).build());
    }

    // 사이즈에 맞다면 NetworkPacket 클래스 반환
    public NetworkPakcetProtoBuf.NetworkPacket assembleClientBuffer(String resourceKey, eNetworkType type,
            byte[] packetBytes)
            throws IOException, io.jsonwebtoken.io.IOException, ClassNotFoundException {
        ClientResource resource = resources.get(resourceKey);
        if (resource == null) {
            throw new RuntimeException();
        }

        ClientNetworkBuffer buffer = resource.getBuffer();
        if (buffer == null) {
            buffer = ClientNetworkBuffer.builder()
                    .tcpBuffer(new ByteArrayOutputStream())
                    .udpBuffer(new ByteArrayOutputStream())
                    .build();
            resource.setBuffer(buffer);
        }

        ByteArrayOutputStream bufferStream = buffer.getTcpBuffer();
        byte[] result = null;
        if (type == eNetworkType.tcp) {
            bufferStream = buffer.getTcpBuffer();
        } else {
            bufferStream = buffer.getUdpBuffer();
        }

        bufferStream.write(packetBytes);
        if (bufferStream.size() == NetworkUtils.TOTAL_MAX_SIZE) {
            result = bufferStream.toByteArray();
            bufferStream.reset();
            return (NetworkPacket) CommonUtils.bytesToObject(result);
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

    public boolean close(String resourceKey) {
        ClientResource clientResource = resoucres.get(resourceKey);
        if (clientResource == null) {
            log.error("클라이언트 자원 존재 안함");
            return false;
        }
        resoucres.remove(resourceKey);

        String connectionKey = clientResource.getConnectionKey();
        ClientConnection clientConnection = connections.get(connectionKey);
        if (clientConnection == null) {
            log.error("클라이언트 커넥션 존재 안함");
            return false;
        }
        connections.remove(connectionKey);

        buffers.remove(connectionKey);
        if (clientConnection.isConnectionTcp()) {
            clientConnection.getTcpChannel().close();
        }

        return true;
    }
}
