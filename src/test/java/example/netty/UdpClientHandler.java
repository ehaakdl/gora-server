package example.netty;

import org.gora.server.common.AesUtils;
import org.gora.server.common.NetworkUtils;
import org.gora.server.model.network.NetworkPackcetProtoBuf.NetworkPacket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

public class UdpClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        byte[] bytes = new byte[msg.content().readableBytes()];
        msg.content().readBytes(bytes);

        NetworkPacket packet = NetworkPacket.parser().parseFrom(bytes);
        byte[] dataRemovePadding = NetworkUtils.removePadding(packet.getData().toByteArray(),
                NetworkUtils.DATA_MAX_SIZE - packet.getDataSize());
        System.out.println("Received: " + AesUtils.decrypt(new String(dataRemovePadding)));

    }
}
