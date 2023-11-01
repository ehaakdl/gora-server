package org.gora.server.component.network;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    private ClientManager clientManager;
    
    // 순환참조로 clientManager 부분은 객체 생성이후에 주입받는다.
    @Autowired
    public void setClientManager(ClientManager clientManager) {
        this.clientManager = clientManager;
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        byte[] recvBytes = new byte[msg.content().readableBytes()];
        msg.content().readBytes(recvBytes);
        
        // NetworkPakcetProtoBuf.NetworkPacket test = (org.gora.server.model.network.NetworkPacketProtoBuf.NetworkPacket) bytesToObject(contentByte);
        // TestProtoBuf.Test dd =  (NetworkTestProtoBuf.NetworkTest) bytesToObject(test.getData().toByteArray());
        // log.info("사이즈 : {}, {}", test.getTotalSize(), test.getData().size());
        // if(test.getTotalSize() != test.getData().size()){
        //     log.error("사이즈 다름: {}, {}", test.getTotalSize(), test.getData().size());
        // }
        
        // contentByte = objectToBytes(test);
        //             ctx.channel().writeAndFlush(new DatagramPacket(
        //             Unpooled.copiedBuffer(contentByte),
        //             new InetSocketAddress("localhost", 11112))).sync();

    }

    
}