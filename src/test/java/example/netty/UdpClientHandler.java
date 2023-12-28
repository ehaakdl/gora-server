package example.netty;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.gora.server.common.NetworkUtils;
import org.gora.server.model.network.NetworkPacketProtoBuf.NetworkPacket;
import org.gora.server.model.network.TestProtoBuf.Test;
import org.gora.server.model.network.eServiceType;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

public class UdpClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private static ByteArrayOutputStream recv = new ByteArrayOutputStream();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        byte[] bytes = new byte[msg.content().readableBytes()];
        msg.content().readBytes(bytes);
        recv.write(bytes);

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
                String encryptChannelId = packet.getChannelId();
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
}
