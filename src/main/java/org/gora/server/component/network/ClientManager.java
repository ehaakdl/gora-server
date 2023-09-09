package org.gora.server.component.network;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.gora.server.common.CommonUtils;
import org.gora.server.common.eEnv;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientManager {
    // key: ip, value: 미정
    private final static Map<String, String> clients = new ConcurrentHashMap<>(Integer.parseInt(CommonUtils.getEnv(eEnv.MAX_DEFAULT_QUE_SZ, eEnv.getDefaultStringTypeValue(eEnv.MAX_DEFAULT_QUE_SZ))));

    public static boolean contain(String key){
        if(key==null){
            return false;
        }

        return clients.containsKey(key);
    }

    public static String put(String clientIp){
        clients.put(clientIp, clientIp);
        return clientIp;
    }

    public static String get(String clientIp){
        return clients.get(clientIp);
    }

    public static boolean remove(String clientIp){
        if(!contain(clientIp)){
            return false;
        }
        clients.remove(clientIp);
        return true;
    }
}
