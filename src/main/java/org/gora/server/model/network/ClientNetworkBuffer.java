package org.gora.server.model.network;

import java.io.ByteArrayOutputStream;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClientNetworkBuffer {
    private ByteArrayOutputStream tcpRecvBuffer;
    private ByteArrayOutputStream tcpDataBuffer;
    private ByteArrayOutputStream udpRecvBuffer;
    private ByteArrayOutputStream udpDataBuffer;
}
