package org.gora.server.model.network;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

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
    private Map<String, ClientDataBuffer> tcpDataBufferMap;
    private Map<String,ClientDataBuffer> udpDataBufferMap;
    private Long userSeq;

    public static ClientResource create(ClientConnection connection) {

        return ClientResource.builder().udpBuffer(new ByteArrayOutputStream()).tcpBuffer(new ByteArrayOutputStream())
                .tcpDataBufferMap(new HashMap<>())
                .udpDataBufferMap(new HashMap<>())
                .connection(connection).build();
    }
}
