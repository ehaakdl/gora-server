package org.gora.server.component.network;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.gora.server.common.CommonUtils;
import org.gora.server.common.eEnv;
import org.gora.server.model.CommonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TcpClientManager {
    @Autowired
    private ObjectMapper objectMapper;
    private final Map<String, ChannelHandlerContext> clients = new ConcurrentHashMap<>(Integer.parseInt(CommonUtils.getEnv(eEnv.MAX_DEFAULT_QUE_SZ, eEnv.getDefaultStringTypeValue(eEnv.MAX_DEFAULT_QUE_SZ))));

    public boolean send(CommonData data){
        ChannelHandlerContext channelHandlerContext = clients.get(data.getKey());
        if(channelHandlerContext == null){
            return false;
        }

        ByteBuf sendBuf= CommonData.converByteBuf(data, objectMapper);
        if(sendBuf == null){
            return false;
        }
        
        channelHandlerContext.writeAndFlush(sendBuf).addListener(future -> {
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
