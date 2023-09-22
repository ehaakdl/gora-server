package org.gora.server.component.network.pipline;

import java.util.List;

import org.gora.server.common.NetworkUtils;
import org.gora.server.model.CommonData;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServerTcpMessageDecoder extends ByteToMessageDecoder {
    private final ObjectMapper objectMapper;
    private static StringBuilder assemble = new StringBuilder();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf recvMsg, List<Object> outMsg) throws Exception {
        ByteBuf recvByteBuf = (ByteBuf) recvMsg;
        byte[] receiveByte = new byte[recvByteBuf.readableBytes()];
        recvMsg.readBytes(receiveByte);

        
        String recvJson = new String(receiveByte);
        assemble.append(recvJson);
        int index = assemble.indexOf(NetworkUtils.EOF_STRING);
        if (index < 0) {
            return;
        }

        
        String targetSerialize = assemble.substring(0, index);
        assemble.delete(0, index + NetworkUtils.EOF_STRING.length());

        CommonData commonData;
        try {
            commonData = objectMapper.readValue(targetSerialize, CommonData.class);
        } catch (Exception e) {
            log.info("[TCP] 잘못된 수신 패킷 왔습니다.", e);
            log.error("[TCP] 잘못된 수신 패킷 왔습니다.", e);
            throw new UnsupportedOperationException(e);
        }

        outMsg.add(commonData);
    }

}
