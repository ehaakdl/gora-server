package org.gora.server.model.network;

import org.gora.server.model.ClientConnection;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@Setter
public class ClientResource {
    private ClientConnection connection;
    private ClientNetworkBuffer buffer;
    private Long userSeq;

    public static ClientResource create(ClientConnection connection) {
        ClientNetworkBuffer buffer = ClientNetworkBuffer.create();
        return ClientResource.builder().buffer(buffer).connection(connection).build();
    }
}
