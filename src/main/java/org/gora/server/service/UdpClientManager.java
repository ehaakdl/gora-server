package org.gora.server.service;

import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;
import org.gora.server.common.CommonUtils;
import org.gora.server.common.eEnv;
import org.gora.server.runner.UdpClient;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class UdpClientManager {
    private static final Map<String, UdpClient> UDP_CLIENTS = new HashMap<>(Integer.parseInt(CommonUtils.getEnv(eEnv.MAX_DEFAULT_QUE_SZ, "100")));

    public static boolean contain(String key){
        if(key==null){
            return false;
        }

        return UDP_CLIENTS.containsKey(key);
    }

    public static String connect(String clientIp){
        String key = CommonUtils.replaceUUID();
        UdpClient udpClient;

        try {
            udpClient = new UdpClient(clientIp, Integer.parseInt(CommonUtils.getEnv(eEnv.CLIENT_PORT, "11112")));
        } catch (InterruptedException e) {
            log.error("udp client connect fail");
            log.error(CommonUtils.getStackTraceElements(e));
            return null;
        }

        UDP_CLIENTS.put(key, udpClient);

        return key;
    }

    public static ChannelFuture getChannelFuture(String key){
        if(!contain(key)){
            return null;
        }

        return UDP_CLIENTS.get(key).getChannelFuture();
    }

    public static boolean shutdown(String key){
        if(!contain(key)){
            return false;
        }

        UdpClient udpClient = UDP_CLIENTS.get(key);
        udpClient.shutdown();

        UDP_CLIENTS.remove(key);

        return true;
    }
}
