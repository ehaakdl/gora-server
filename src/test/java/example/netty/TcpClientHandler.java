package example.netty;

import java.io.IOException;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class TcpClientHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg)
            throws StreamReadException, DatabindException, IOException {
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] receiveByte = new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(byteBuf.readerIndex(), receiveByte);

        System.out.println("수신: " + receiveByte.toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
