package org.gora.server.component.network;

import io.netty.channel.ChannelFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gora.server.common.CommonUtils;
import org.gora.server.common.eEnv;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class UdpClientManager {
    private final Map<String, UdpClient> UDP_CLIENTS;
    private final UdpInboundHandler udpInboundHandler;

    public UdpClientManager(UdpInboundHandler udpInboundHandler) {
        this.UDP_CLIENTS = new HashMap<>(Integer.parseInt(CommonUtils.getEnv(eEnv.MAX_DEFAULT_QUE_SZ, eEnv.getDefaultStringTypeValue(eEnv.MAX_DEFAULT_QUE_SZ))));
        this.udpInboundHandler = udpInboundHandler;
    }

    public boolean contain(String key){
        if(key==null){
            return false;
        }

        return UDP_CLIENTS.containsKey(key);
    }

    public String connect(String clientIp){
        String key = CommonUtils.replaceUUID();
        UdpClient udpClient;

        try {
            udpClient = new UdpClient(clientIp, Integer.parseInt(CommonUtils.getEnv(eEnv.CLIENT_PORT, eEnv.getDefaultStringTypeValue(eEnv.CLIENT_PORT))), udpInboundHandler);
        } catch (InterruptedException e) {
            log.error("udp client connect fail");
            log.error(CommonUtils.getStackTraceElements(e));
            return null;
        }

        this.UDP_CLIENTS.put(key, udpClient);

        return key;
    }

    public ChannelFuture getChannelFuture(String key){
        if(!contain(key)){
            return null;
        }

        return UDP_CLIENTS.get(key).getChannelFuture();
    }

    public boolean shutdown(String key){
        if(!contain(key)){
            return false;
        }

        UdpClient udpClient = UDP_CLIENTS.get(key);
        udpClient.shutdown();

        UDP_CLIENTS.remove(key);

        return true;
    }
}
