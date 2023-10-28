package org.gora.server.model.network;

import java.io.ByteArrayOutputStream;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClientNetworkBuffer {
    private ByteArrayOutputStream tcpBuffer;
    private ByteArrayOutputStream udpBuffer;
}
