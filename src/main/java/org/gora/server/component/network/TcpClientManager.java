package org.gora.server.component.network;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.gora.server.common.CommonUtils;
import org.gora.server.common.eEnv;
import org.gora.server.model.CommonData;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TcpClientManager {
    private final Map<String, ChannelHandlerContext> clients = new ConcurrentHashMap<>(Integer.parseInt(CommonUtils.getEnv(eEnv.MAX_DEFAULT_QUE_SZ, eEnv.getDefaultStringTypeValue(eEnv.MAX_DEFAULT_QUE_SZ))));

    public boolean send(CommonData data){
        ChannelHandlerContext channelHandlerContext = clients.get(data.getKey());
        if(channelHandlerContext == null){
            return false;
        }
        
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] message;
        try {
            message = objectMapper.writeValueAsString(data).getBytes();
        } catch (JsonProcessingException e) {
            log.error("송신 데이터 파싱실패");
            log.error(CommonUtils.getStackTraceElements(e));
            return false;
        }
        ByteBuf buffer = Unpooled.wrappedBuffer(message);
        channelHandlerContext.writeAndFlush(buffer).addListener(future -> {
            if(!future.isSuccess()){
                log.error("송신 실패");
                log.error(CommonUtils.getStackTraceElements(future.cause()));
            }
        });
        return true;
    }

    public boolean close(String key){
        ChannelHandlerContext channelHandlerContext = clients.get(key);
        if(channelHandlerContext == null){
            return false;
        }

        channelHandlerContext.close();
        return true;
    }

    public String  put(ChannelHandlerContext channelHandlerContext){
        String key = CommonUtils.replaceUUID();
        clients.put(key, channelHandlerContext);
        return key;
    }

    public boolean contain(String key){
        if(key == null){
            return false;
        }
        return clients.containsKey(key);
    }
}
