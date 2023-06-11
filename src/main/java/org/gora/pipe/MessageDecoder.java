package org.gora.pipe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.gora.model.Message;
import org.gora.model.CommonData;
import org.gora.utils.CommonUtils;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class MessageDecoder extends MessageToMessageDecoder<DatagramPacket> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket packet, List<Object> out) {

        InetSocketAddress sender = packet.sender();

        ByteBuf content = packet.content();
        int readableBytes = content.readableBytes();
        if (readableBytes <= 0) {
            return;
        }

        String contentToString = content.toString(CharsetUtil.UTF_8);
        Message message;
        try {
            message = objectMapper.readValue(contentToString, Message.class);
        } catch (JsonProcessingException e) {
            log.error("[udp] 수신 데이터 지정된 클래스로 치환 실패");
            log.error(CommonUtils.getStackTraceElements(e));
            return;
        }
        content.readerIndex(content.readerIndex() + content.readableBytes());

        CommonData outMessage = new CommonData(message, sender);
        out.add(outMessage);
    }

}
