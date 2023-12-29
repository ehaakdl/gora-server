package org.gora.server.model;

import org.gora.server.model.network.eRouteServiceType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PacketRouterDTO {
    private String chanelId;
    private Object data;
    private final eRouteServiceType type;

    public static PacketRouterDTO create(eRouteServiceType type, Object data, String chanelId) {
        return PacketRouterDTO.builder().chanelId(chanelId).data(data).type(type).build();
    }
}
