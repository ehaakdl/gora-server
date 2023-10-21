package org.gora.server.model;

import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ClientConnection{
    private ChannelHandlerContext tcpChannel;
    private String clientIp;

    public static ClientConnection createTcp(String clientIp, ChannelHandlerContext channel){
        return new ClientConnection(channel, clientIp);
    }

    public static ClientConnection createUdp(String clientIp){
        return new ClientConnection(null, clientIp);
    }
    
    public boolean isConnectionTcp(){
        if(tcpChannel == null){
            return false;
        }

        return tcpChannel.channel().isActive();
    }
}