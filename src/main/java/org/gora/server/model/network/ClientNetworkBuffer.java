package org.gora.server.model.network;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClientNetworkBuffer {
    private ByteArrayOutputStream tcpRecvBuffer;
    private Map<String, ClientNetworkDataWrapper> tcpDataWrapper;
    private Map<String, ClientNetworkDataWrapper> udpDataWrapper;
    private ByteArrayOutputStream udpRecvBuffer;

    public static ClientNetworkBuffer create(){
        return ClientNetworkBuffer.builder()
                    .tcpRecvBuffer(new ByteArrayOutputStream())
                    .udpRecvBuffer(new ByteArrayOutputStream())
                    .udpDataWrapper(new HashMap<>())
                    .tcpDataWrapper(new HashMap<>())
                    .build();
    }
    
    public void putDataWrapper(String identify, eNetworkType type, ClientNetworkDataWrapper value){
        if(type == eNetworkType.tcp){
            tcpDataWrapper.put(identify, value);
        }else{
            udpDataWrapper.put(identify, value);
        }   
    }

    public ClientNetworkDataWrapper getDataWrapper(String identify, eNetworkType type){
        if(type == eNetworkType.tcp){
            return tcpDataWrapper.get(identify);
        }else{
            return udpDataWrapper.get(identify);
        }   
    }

    public void removeDataWrapper(String identify, eNetworkType networkType) {
        if(networkType == eNetworkType.tcp){
            tcpDataWrapper.remove(identify);
        }else{
            udpDataWrapper.remove(identify);
        }
    }

    public boolean containDataWrapper(String identify, eNetworkType networkType) {
        if(networkType == eNetworkType.tcp){
            return tcpDataWrapper.containsKey(identify);
        }else{
            return udpDataWrapper.containsKey(identify);
        }
    }
}
