package org.gora.server.component.network;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.gora.server.common.CommonUtils;
import org.gora.server.common.eEnv;
import org.gora.server.model.ClientConnection;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientManager {
    private final static Map<String, ClientConnection> clients = new ConcurrentHashMap<>(Integer.parseInt(CommonUtils.getEnv(eEnv.MAX_DEFAULT_QUE_SZ, eEnv.getDefaultStringTypeValue(eEnv.MAX_DEFAULT_QUE_SZ))));

    public static boolean contain(String key){
        if(key==null){
            return false;
        }

        return clients.containsKey(key);
    }

    public static void put(String key, ClientConnection value){
        clients.put(key, value);
    }

    public static ClientConnection get(String key){
        return clients.get(key);
    }

    public static boolean remove(String key){
        if(!contain(key)){
            return false;
        }
        clients.remove(key);
        return true;
    }
}
