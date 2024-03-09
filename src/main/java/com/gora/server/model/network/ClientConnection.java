package com.gora.server.model.network;

import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ClientConnection {
    private ChannelHandlerContext tcpChannel;
    private String clientIp;

    public ClientConnection(ChannelHandlerContext channel) {
        this.tcpChannel = channel;
    }

    public static ClientConnection createTcp(ChannelHandlerContext channel) {
        return new ClientConnection(channel);
    }

    public static ClientConnection createUdp(String clientIp) {
        return new ClientConnection(null, clientIp);
    }

    public boolean isConnectionTcp() {
        if (tcpChannel == null) {
            return false;
        }

        return tcpChannel.channel().isActive();
    }
}