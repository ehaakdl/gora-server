package org.gora.server.component.network.pipline;

import java.net.InetSocketAddress;
import java.util.List;

import org.gora.server.component.network.ClientManager;
import org.gora.server.model.network.eNetworkType;
import org.gora.server.model.ClientConnection;
import org.gora.server.model.network.NetworkPakcetProtoBuf.NetworkPacket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ServerTcpMessageDecoder extends ByteToMessageDecoder {
    private final ClientManager clientManager;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf recvMsg, List<Object> outMsg) throws Exception {
        ByteBuf recvByteBuf = (ByteBuf) recvMsg;
        byte[] recvBytes = new byte[recvByteBuf.readableBytes()];
        // 이 코드 왜있는건지 알아보기
        recvMsg.readBytes(recvBytes);

        String chanelId = ctx.channel().id().asLongText();
        NetworkPacket packet;
        String clientIp = ctx.channel().remoteAddress().toString();
        ClientConnection connection = ClientConnection.createTcp(clientIp, ctx);
        if(!clientManager.existsResource(chanelId)){
            clientManager.createResource(chanelId, connection);
        }
        
        try {
            packet = clientManager.assembleClientBuffer(chanelId, eNetworkType.tcp, recvBytes);
        } catch (Exception e) {
            // 무조건 고정된 사이즈로 들어오기 때문에 캐스팅 실패할수가없다.
            log.error("위조된 패킷이 온걸로 추정됩니다.");
            ctx.channel().closeFuture();
            return;
        }
        if (packet == null) {
            return;
        } else {
            outMsg.add(packet);
        }
    }

}
