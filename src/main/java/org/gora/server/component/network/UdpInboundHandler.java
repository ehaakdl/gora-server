package org.gora.server.component.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;

import org.gora.server.component.LoginTokenProvider;
import org.gora.server.model.network.NetworkPacketProtoBuf;
import org.gora.server.model.network.PlayerCoordinateProtoBuf;
import org.gora.server.model.network.PlayerCoordinateProtoBuf.PlayerCoordinate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.io.IOException;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@ChannelHandler.Sharable
@RequiredArgsConstructor
@Slf4j
// 파이프라인에 decoder 구성을 추가하였으나, decoder클래스를 거치지 않는 문제 발생
// decoder 역할은 임시로 handler에서 담당
public class UdpInboundHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private final ObjectMapper objectMapper;
    private final LoginTokenProvider loginTokenProvider;
    private ClientManager clientManager;
    private static final StringBuilder assemble = new StringBuilder();
    
    // 순환참조로 clientManager 부분은 객체 생성이후에 주입받는다.
    @Autowired
    public void setClientManager(ClientManager clientManager) {
        this.clientManager = clientManager;
    }
    public static Object bytesToObject(byte[] bytes) throws IOException, ClassNotFoundException, java.io.IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return ois.readObject();
        }
    }
    public static byte[] objectToBytes(Object obj) throws IOException, java.io.IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            oos.flush();
            return bos.toByteArray();
        }
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        byte[] contentByte = new byte[msg.content().readableBytes()];
            msg.content().readBytes(contentByte);
        NetworkPacketProtoBuf.NetworkPacket test = (org.gora.server.model.network.NetworkPacketProtoBuf.NetworkPacket) bytesToObject(contentByte);
        PlayerCoordinateProtoBuf.PlayerCoordinate dd =  (PlayerCoordinate) bytesToObject(test.getData().toByteArray());
        
        contentByte = objectToBytes(test);
                    ctx.channel().writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer(contentByte),
                    new InetSocketAddress("localhost", 11112))).sync();

    }

    
}