package org.gora.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.gora.server.model.CommonData;

public class UdpServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        CommonData content = objectMapper.readValue(msg.content().array(), CommonData.class);
        System.out.println("Received: " + content);
    }
}