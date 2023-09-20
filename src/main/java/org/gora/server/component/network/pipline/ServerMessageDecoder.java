package org.gora.server.component.network.pipline;

import java.util.List;

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
public class ServerMessageDecoder extends ByteToMessageDecoder{
    private final ObjectMapper objectMapper;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf recvMsg, List<Object> outMsg) throws Exception {
        // TODO 수신된 사이즈보고 다왔는지 체크하는 로직 필요 
        // Bug 수신된 사이즈가 다름 데이터가 많이들어올 때 json형태가 아닌게 들어옴 이전 데이터거나 그런거 같음
        ByteBuf recvByteBuf = (ByteBuf) recvMsg;
        byte[] receiveByte = new byte[recvByteBuf.readableBytes()];
        recvMsg.readBytes(receiveByte);
        log.info(Long.toString(receiveByte.length));
        CommonData commonData;
        try{
            commonData = objectMapper.readValue(receiveByte, CommonData.class);
        }catch(Exception e){
            log.info("[TCP] 잘못된 수신 패킷 왔습니다.", e);
              // todo 수신된 데이터를 재조립 가능해야함
            log.error("에러 json:{}", new String(receiveByte));
            log.error("size:{}", receiveByte.length);
            log.error("[TCP] 잘못된 수신 패킷 왔습니다.", e);
            throw new UnsupportedOperationException(e);
        }

        outMsg.add(commonData);
    }
    
}
