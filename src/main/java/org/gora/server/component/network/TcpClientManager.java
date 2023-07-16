package org.gora.server.component.network;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.gora.server.common.CommonUtils;
import org.gora.server.common.eEnv;
import org.gora.server.model.CommonData;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class TcpClientManager {
    private final Map<String, ChannelHandlerContext> clients = new ConcurrentHashMap<>(Integer.parseInt(CommonUtils.getEnv(eEnv.MAX_DEFAULT_QUE_SZ, eEnv.getDefaultStringTypeValue(eEnv.MAX_DEFAULT_QUE_SZ))));

    public boolean send(CommonData data){
        ChannelHandlerContext channelHandlerContext = clients.get(data.getKey());
        if(channelHandlerContext == null){
            return false;
        }

        channelHandlerContext.writeAndFlush(data);
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
