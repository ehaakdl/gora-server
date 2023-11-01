package org.gora.server.model;

import org.gora.server.model.network.eServiceType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TransportData {
    private String chanelId;
    private Object packet;
    private eServiceType type;
}
