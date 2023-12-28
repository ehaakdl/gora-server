package example.netty;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.gora.server.common.NetworkUtils;
import org.gora.server.model.network.NetworkPacketProtoBuf.NetworkPacket;
import org.gora.server.model.network.TestProtoBuf.Test;
import org.gora.server.model.network.eServiceType;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class TcpClientHandler extends SimpleChannelInboundHandler<Object> {
    private static ByteArrayOutputStream recv = new ByteArrayOutputStream();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg)
            throws StreamReadException, DatabindException, IOException {
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] receiveByte = new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(byteBuf.readerIndex(), receiveByte);

        recv.write(receiveByte);

        int packetCount = recv.size() / NetworkUtils.TOTAL_MAX_SIZE;
        int remainBytes = recv.size() % NetworkUtils.TOTAL_MAX_SIZE;
        int from;
        int to;
        byte[] convertBytes;
        for (int count = 0; count < packetCount; count++) {
            from = count * NetworkUtils.TOTAL_MAX_SIZE;
            to = (count + 1) * NetworkUtils.TOTAL_MAX_SIZE;
            convertBytes = Arrays.copyOfRange(recv.toByteArray(), from, to);
            NetworkPacket packet = NetworkPacket.parseFrom(convertBytes);
            eServiceType serviceType = eServiceType.convert(packet.getType());
            byte[] refineDataByte = NetworkUtils.removePadding(packet.getData().toByteArray(),
                    NetworkUtils.DATA_MAX_SIZE - packet.getDataSize());
            if (serviceType == eServiceType.udp_initial) {
                String encryptChannelId = new String(refineDataByte);
                System.out
                        .println(encryptChannelId);
                UdpClient.channelId = encryptChannelId;
            } else {
                if (serviceType == eServiceType.test) {
                    Test test = Test.parseFrom(refineDataByte);
                    System.out.println("Received: " + test.getMsg().toStringUtf8());
                } else {
                    System.out.println("처리할수 없는 서비스 유형패킷입니다.");
                }

            }

        }
        if (remainBytes > 0) {
            convertBytes = Arrays.copyOfRange(recv.toByteArray(), packetCount * NetworkUtils.TOTAL_MAX_SIZE,
                    recv.size());
            recv.reset();
            recv.write(convertBytes);
        } else {
            recv.reset();
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
