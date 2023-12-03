package org.gora.server.model.network;

import java.io.ByteArrayOutputStream;

import org.gora.server.model.ClientConnection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class ClientResource {
    private ClientConnection connection;
    private ByteArrayOutputStream tcpBuffer;
    private ByteArrayOutputStream udpBuffer;
    private Long userSeq;

    public static ClientResource create(ClientConnection connection) {

        return ClientResource.builder().udpBuffer(new ByteArrayOutputStream()).tcpBuffer(new ByteArrayOutputStream())
                .connection(connection).build();
    }
}
