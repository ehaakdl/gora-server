package org.gora.server.model.network;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommonData {
    private Object data;
    private eServiceType type;
    private String key;
}