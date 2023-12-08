package org.gora.server.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TransportData {
    private String chanelId;
    private byte[] data;
    private eRouteServiceType type;

    public static TransportData create(eRouteServiceType type, byte[] data, String chanelId) {
        return TransportData.builder().chanelId(chanelId).data(data).type(type).build();
    }
}
