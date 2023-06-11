package org.gora.pipe;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.gora.model.CommonData;
import org.gora.utils.CommonUtils;

import java.nio.charset.StandardCharsets;

@Slf4j
public class MessageHandler extends SimpleChannelInboundHandler<CommonData> {
    private void read(ChannelHandlerContext ctx, CommonData commonData) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String  content = objectMapper.writeValueAsString(commonData.getData());
        ByteBuf contentBuf = Unpooled.wrappedBuffer(content.getBytes(StandardCharsets.UTF_8));

        ctx
                .channel()
                .writeAndFlush(new DatagramPacket(contentBuf, commonData.getSender()))
                .addListener((ChannelFutureListener) future -> {
//                    성공여부체크
                });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        log.error(CommonUtils.getStackTraceElements(cause));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CommonData msg) throws JsonProcessingException {
        read(ctx, msg);
    }


}
