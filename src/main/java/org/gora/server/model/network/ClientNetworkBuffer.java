package org.gora.server.model.network;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClientNetworkBuffer {
    private ByteArrayOutputStream tcpRecvBuffer;
    private Map<String, ClientNetworkDataWrapper> tcpDataWrapper;
    private Map<String, ClientNetworkDataWrapper> udpDataWrapper;
    private List<String> tcpIdentifyList;
    private List<String> udpIdentifyList;
    private ByteArrayOutputStream udpRecvBuffer;

    public static ClientNetworkBuffer create(){
        return ClientNetworkBuffer.builder()
                    .tcpRecvBuffer(new ByteArrayOutputStream())
                    .udpRecvBuffer(new ByteArrayOutputStream())
                    .udpDataWrapper(new HashMap<>())
                    .tcpDataWrapper(new HashMap<>())
                    .tcpIdentifyList(new ArrayList<>())
                    .udpIdentifyList(new ArrayList<>())
                    .build();
    }
    
    public void putDataWrapper(String identify, eNetworkType type, ClientNetworkDataWrapper value){
        if(type == eNetworkType.tcp){
            tcpDataWrapper.put(identify, value);
            tcpIdentifyList.add(identify);
        }else{
            udpDataWrapper.put(identify, value);
            udpIdentifyList.add(identify);
        }   
    }

    public ClientNetworkDataWrapper getDataWrapper(String identify, eNetworkType type){
        if(type == eNetworkType.tcp){
            return tcpDataWrapper.getOrDefault(identify, null);
        }else{
            return udpDataWrapper.getOrDefault(identify, null);
        }   
    }

    public void removeDataWrapper(String identify, eNetworkType networkType) {
        if(networkType == eNetworkType.tcp){
            tcpDataWrapper.remove(identify);
            tcpIdentifyList.remove(identify);
        }else{
            udpDataWrapper.remove(identify);
            tcpIdentifyList.remove(identify);
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
