package org.gora.server.model.network;

import java.io.ByteArrayOutputStream;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ClientNetworkDataWrapper {
    private ByteArrayOutputStream buffer;
    private long createdAt;
    private int totalSize;
    
    public static ClientNetworkDataWrapper create() {
        return ClientNetworkDataWrapper.builder()
        .createdAt(System.currentTimeMillis())
        .buffer(new ByteArrayOutputStream())
        .build();
    }
}