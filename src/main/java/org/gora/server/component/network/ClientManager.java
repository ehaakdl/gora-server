package org.gora.server.component.network;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.gora.server.common.CommonUtils;
import org.gora.server.common.Env;
import org.gora.server.model.ClientConnection;
import org.gora.server.model.eProtocol;
import org.gora.server.model.network.NetworkPacket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientManager {
    @Value("${app.udp_client_port}")
    private int udpClientPort;
    private final ObjectMapper objectMapper;
    private final UdpServer udpServer;
    private final static Map<String, ClientConnection> clients = new ConcurrentHashMap<>(
            Integer.parseInt(System.getenv(Env.MAX_DEFAULT_QUE_SZ)));

    public static boolean contain(String key) {
        if (key == null) {
            return false;
        }

        return clients.containsKey(key);
    }

    public static void put(String key, ClientConnection value) {
        clients.put(key, value);
    }

    public static ClientConnection get(String key) {
        return clients.get(key);
    }

    public static boolean remove(String key) {
        if (!contain(key)) {
            return false;
        }
        clients.remove(key);
        return true;
    }

    public boolean send(NetworkPacket data) {
        ClientConnection clientConnection = ClientManager.get(data.getKey());
        if (clientConnection == null) {
            log.error("클라이언트 존재 안함");
            return false;
        }

        ByteBuf sendBuf = NetworkPacket.convertByteBuf(data, objectMapper);
        if (sendBuf == null) {
            log.error("송신 데이터 파싱 실패");
            return false;
        }

        if (data.getProtocol() == eProtocol.tcp) {
            if (!clientConnection.isConnectionTcp()) {
                log.error("클라이언트와 TCP 연결 안됨");
                return false;
            }

            clientConnection.getTcpChannel().writeAndFlush(sendBuf).addListener(future -> {
                if (!future.isSuccess()) {
                    log.error("송신 실패");
                    log.error(CommonUtils.getStackTraceElements(future.cause()));
                }
            });
        } else {
            udpServer.send(clientConnection.getClientIp(), udpClientPort, sendBuf.array());
        }

        return true;
    }

    public boolean close(String key) {
        ClientConnection clientConnection = ClientManager.get(key);
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
