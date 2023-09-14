package org.gora.server.component.network;

import org.gora.server.common.CommonUtils;
import org.gora.server.model.ClientConnection;
import org.gora.server.model.CommonData;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class TcpClientManager {
    private final ObjectMapper objectMapper;

    public boolean send(CommonData data){
        ClientConnection clientConnection = ClientManager.get(data.getKey());
        if(clientConnection == null){
            log.error("클라이언트 존재 안함");
            return false;
        }

        ByteBuf sendBuf= CommonData.convertByteBuf(data, objectMapper);
        if(sendBuf == null){
            log.error("송신 데이터 파싱 실패");
            return false;
        }
        
        if(!clientConnection.isConnectionTcp()){
            log.error("클라이언트와 TCP 연결 안됨");
            return false;
        }
        
        clientConnection.getTcpChannel().writeAndFlush(sendBuf).addListener(future -> {
            if(!future.isSuccess()){
                log.error("송신 실패");
                log.error(CommonUtils.getStackTraceElements(future.cause()));
            }
        });

        return true;
    }

    public boolean close(String key){
        ClientConnection clientConnection = ClientManager.get(key);
        if(clientConnection == null){
            log.error("클라이언트 존재 안함");
            return false;
        }
        if(clientConnection.isConnectionTcp()){
            clientConnection.getTcpChannel().close();
        }
        
        return true;
    }
}
