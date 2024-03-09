package com.gora.server.component.network.handler.inbound;

import java.util.ArrayList;
import java.util.List;

import com.gora.server.common.utils.CommonUtils;
import com.gora.server.component.network.ClientManager;
import com.gora.server.model.PacketRouterDTO;
import com.gora.server.model.network.eNetworkType;
import com.gora.server.model.network.eServiceType;

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
        List<PacketRouterDTO> PacketRouterDTOs;

        // 패킷 조립
        try {
            PacketRouterDTOs = ClientManager.assemblePacket(chanelId, eNetworkType.tcp, recvBytes);
        } catch (Exception e) {
            // 무조건 고정된 사이즈로 들어오기 때문에 캐스팅 실패할수가없다.
            log.error("위조된 패킷이 온걸로 추정됩니다. {}", CommonUtils.getStackTraceElements(e));
            log.info("패킷 위조 예상아이디 :{}", chanelId);
            PacketRouterDTOs = new ArrayList<>();
            PacketRouterDTOs.add(PacketRouterDTO.create(eServiceType.close_client, null, chanelId));
        }

        if (PacketRouterDTOs.isEmpty()) {
            return;
        }

        outMsg.addAll(PacketRouterDTOs);
    }

}
