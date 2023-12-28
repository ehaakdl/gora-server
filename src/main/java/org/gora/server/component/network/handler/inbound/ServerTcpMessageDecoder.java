package org.gora.server.component.network.handler.inbound;

import java.util.ArrayList;
import java.util.List;

import org.gora.server.common.CommonUtils;
import org.gora.server.component.network.ClientManager;
import org.gora.server.model.TransportData;
import org.gora.server.model.network.eNetworkType;
import org.gora.server.model.network.eRouteServiceType;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ServerTcpMessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf recvMsg, List<Object> outMsg) throws Exception {
        ByteBuf recvByteBuf = (ByteBuf) recvMsg;
        byte[] recvBytes = new byte[recvByteBuf.readableBytes()];
        recvMsg.readBytes(recvBytes);

        String chanelId = ctx.channel().id().asLongText();
        List<TransportData> transportDatas;

        // 패킷 조립
        try {
            transportDatas = ClientManager.assemblePacket(chanelId, eNetworkType.tcp, recvBytes);
        } catch (Exception e) {
            // 무조건 고정된 사이즈로 들어오기 때문에 캐스팅 실패할수가없다.
            log.error("위조된 패킷이 온걸로 추정됩니다. {}", CommonUtils.getStackTraceElements(e));
            log.info("패킷 위조 예상아이디 :{}", chanelId);
            transportDatas = new ArrayList<>();
            transportDatas.add(TransportData.create(eRouteServiceType.close_client, null, chanelId));
        }

        if (transportDatas.isEmpty()) {
            return;
        } else {
            outMsg.addAll(transportDatas);
        }
    }

}
