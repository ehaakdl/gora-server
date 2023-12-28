package org.gora.server.model;

import org.gora.server.model.network.eRouteServiceType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TransportData {
    private String chanelId;
    private Object data;
    private final eRouteServiceType type;

    public static TransportData create(eRouteServiceType type, Object data, String chanelId) {
        return TransportData.builder().chanelId(chanelId).data(data).type(type).build();
    }
}
