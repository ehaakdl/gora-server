package org.gora.server.model;

import org.gora.server.model.network.NetworkPakcetProtoBuf.NetworkPacket;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TransportData {
    private String chanelId;
    private NetworkPacket packet;

}
