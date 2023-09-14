package org.gora.server.component.network;

import java.util.UUID;

import org.gora.server.model.ClientConnection;
import org.gora.server.model.CommonData;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@ChannelHandler.Sharable
public class TcpServerHandler extends ChannelInboundHandlerAdapter {
    private final ObjectMapper objectMapper;
    private final TcpClientManager tcpClientManager;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] receiveByte = new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(byteBuf.readerIndex(), receiveByte);
        CommonData commonData = objectMapper.readValue(receiveByte, CommonData.class);

//        첫 연결인 경우 클라이언트 맵에 추가
        String key = UUID.randomUUID().toString().replace("-", "");
        if (!ClientManager.contain(commonData.getKey())) {
            ClientManager.put(key, ClientConnection.createTcp(key, ctx));
            commonData.setKey(key);
        }
    
        PacketRouter.push(commonData);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    //종료 이벤트
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }
}
