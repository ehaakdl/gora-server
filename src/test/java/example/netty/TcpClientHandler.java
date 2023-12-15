package example.netty;

import java.io.IOException;

import org.gora.server.common.NetworkUtils;
import org.gora.server.model.network.NetworkPackcetProtoBuf.NetworkPacket;
import org.gora.server.model.network.TestProtoBuf.Test;

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

        NetworkPacket packet = NetworkPacket.parser().parseFrom(receiveByte);
        byte[] recvData = NetworkUtils.removePadding(packet.getData().toByteArray(), NetworkUtils.DATA_MAX_SIZE -
                packet.getDataSize());
        Test test = Test.parser().parseFrom(recvData);
        System.out.println("수신: " + test.getMsg().toStringUtf8());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
