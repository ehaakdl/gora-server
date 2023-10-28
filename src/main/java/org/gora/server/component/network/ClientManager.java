package org.gora.server.component.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.gora.server.common.CommonUtils;
import org.gora.server.common.Env;
import org.gora.server.common.NetworkUtils;
import org.gora.server.model.ClientConnection;
import org.gora.server.model.network.ClientNetworkBuffer;
import org.gora.server.model.network.NetworkPakcetProtoBuf.NetworkPacket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientManager {
    @Value("${app.udp_client_port}")
    private int udpClientPort;
    private final ObjectMapper objectMapper;
    private UdpServer udpServer;
    // key는 채널 아이디(네티는 채널에서 고유한 아이디를 가지고있다.)
    private final static Map<String, ClientConnection> clients = new ConcurrentHashMap<>(
            Integer.parseInt(System.getenv(Env.MAX_DEFAULT_QUE_SZ)));

    private final static Map<String, ClientNetworkBuffer> clientsBuffer = new ConcurrentHashMap<>(
            Integer.parseInt(System.getenv(Env.MAX_DEFAULT_QUE_SZ)));

    // 사이즈에 맞다면 NetworkPacket 클래스 반환
    public org.gora.server.model.network.NetworkPakcetProtoBuf.NetworkPacket assembleClientBuffer(String key, eNetworkType type, byte[] packetBytes)
            throws IOException, io.jsonwebtoken.io.IOException, ClassNotFoundException {
        ClientNetworkBuffer buffer = clientsBuffer.get(key);
        if (buffer == null) {
            buffer = ClientNetworkBuffer.builder()
                    .tcpBuffer(new ByteArrayOutputStream())
                    .udpBuffer(new ByteArrayOutputStream())
                    .build();
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

    public boolean contain(String key) {
        if (key == null) {
            return false;
        }

        return clients.containsKey(key);
    }

    public void put(String key, ClientConnection value) {
        clients.put(key, value);
    }

    public ClientConnection get(String key) {
        return clients.get(key);
    }

    public boolean remove(String key) {
        if (!contain(key)) {
            return false;
        }
        clients.remove(key);
        return true;
    }

    public List<String> getAllKeys() {
        return new ArrayList<>((clients.keySet()));
    }

    public boolean send(NetworkPacket data) {
        // ClientConnection clientConnection = clients.get(data.getKey());
        // if (clientConnection == null) {
        //     log.error("클라이언트 존재 안함");
        //     return false;
        // }

        // ByteBuf sendBuf = NetworkPacket.convertByteBuf(data, objectMapper);
        // if (sendBuf == null) {
        //     log.error("송신 데이터 파싱 실패");
        //     return false;
        // }

        // if (data.getProtocol() == eProtocol.tcp) {
        //     if (!clientConnection.isConnectionTcp()) {
        //         log.error("클라이언트와 TCP 연결 안됨");
        //         return false;
        //     }

        //     clientConnection.getTcpChannel().writeAndFlush(sendBuf).addListener(future -> {
        //         if (!future.isSuccess()) {
        //             log.error("송신 실패");
        //             log.error(CommonUtils.getStackTraceElements(future.cause()));
        //         }
        //     });
        // } else {
        //     udpServer.send(clientConnection.getClientIp(), udpClientPort, sendBuf.array());
        // }

        return true;
    }

    public boolean close(String key) {
        ClientConnection clientConnection = clients.get(key);
        if (clientConnection == null) {
            log.error("클라이언트 존재 안함");
            return false;
        }
        if (clientConnection.isConnectionTcp()) {
            clientConnection.getTcpChannel().close();
        }

        return true;
    }
}
