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
    private long appendAt;
    private int totalSize;
    
    public static ClientNetworkDataWrapper create() {
        long nowAt = System.currentTimeMillis();
        return ClientNetworkDataWrapper.builder()
        .createdAt(nowAt)
        .appendAt(nowAt)
        .buffer(new ByteArrayOutputStream())
        .build();
    }
}