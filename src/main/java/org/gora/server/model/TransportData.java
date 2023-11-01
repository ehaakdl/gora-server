package org.gora.server.model;

import org.gora.server.model.network.eServiceType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TransportData {
    private String chanelId;
    private Object data;
    private eServiceType type;

    public static TransportData create(eServiceType type, Object data, String chanelId){
        return TransportData.builder().chanelId(chanelId).data(data).type(type).build();
    }
}
