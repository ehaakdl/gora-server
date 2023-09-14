package org.gora.server.component.network;

import org.gora.server.common.CommonUtils;
import org.gora.server.model.ClientConnection;
import org.gora.server.model.CommonData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UdpClientManager {
    private final ObjectMapper objectMapper;
    @Value("${app.udp_client_port}")
    private int udpClientPort;
    private final UdpServer udpServer;

    public void send(CommonData commonData) {
        byte[] sendBytes;
        try {
            sendBytes = objectMapper.writeValueAsBytes(commonData);
        } catch (JsonProcessingException e) {
            log.error("send 데이터 직렬화 실패");
            log.error(CommonUtils.getStackTraceElements(e));
            throw new RuntimeException();
        }

        ClientConnection clientConnection = ClientManager.get(commonData.getKey());
        if(clientConnection == null){
            throw new RuntimeException("client Ip를 찾을수가 없다.");
        }
        
        udpServer.send(clientConnection.getClientIp(), udpClientPort, sendBytes);
    }
}
