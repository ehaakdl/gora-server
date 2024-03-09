package com.gora.server.model;

import com.gora.server.model.network.eServiceType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PacketRouterDTO {
    private String channelId;
    private Object data;
    private final eServiceType type;

    public static PacketRouterDTO create(eServiceType type, Object data, String chanelId) {
        return PacketRouterDTO.builder().channelId(chanelId).data(data).type(type).build();
    }
}
