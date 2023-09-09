package example.netty;



import java.io.IOException;

import org.gora.server.model.CommonData;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws StreamReadException, DatabindException, IOException {
        // ObjectMapper objectMapper = new ObjectMapper();
        // ByteBuf byteBuf = (ByteBuf) msg;
        // byte[] receiveByte = new byte[byteBuf.readableBytes()];
        // byteBuf.getBytes(byteBuf.readerIndex(), receiveByte);
        // CommonData commonData = objectMapper.readValue(receiveByte, CommonData.class);

        Channel channel = ctx.channel();
        channel.writeAndFlush("Response : '" + msg + "' received\n");

        if ("quit".equals(msg)) {
            ctx.close();
        }
    }
}