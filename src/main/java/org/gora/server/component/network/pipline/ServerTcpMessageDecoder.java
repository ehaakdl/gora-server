package org.gora.server.component.network.pipline;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.gora.server.model.network.NetworkPakcetProtoBuf.NetworkPacket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RequiredArgsConstructor
@Slf4j
public class ServerTcpMessageDecoder extends ByteToMessageDecoder {
    private ByteArrayOutputStream bufferStream = new ByteArrayOutputStream();
    private long expireTime;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf recvMsg, List<Object> outMsg) throws Exception {
        ByteBuf recvByteBuf = (ByteBuf) recvMsg;
        byte[] recvBytes = new byte[recvByteBuf.readableBytes()];
        // 이 코드 왜있는건지 알아보기
        recvMsg.readBytes(recvBytes);
        
        NetworkPacket packet;
        
        // if(recvBytes.length < NetworkUtils.TAIL.length){
        //     bufferStream.write(recvBytes);
        // }else {
        //     bufferStream.write(recvBytes);
           
            
            
        //     packet = (NetworkPacket) CommonUtils.bytesToObject(newBuffer);
        //     outMsg.add(packet);
        // }

        

        
        
        // NetworkTestProtoBuf.NetworkTest dd =  (NetworkTestProtoBuf.NetworkTest) bytesToObject(test.getData().toByteArray());
        // log.info("사이즈 : {}, {}", test.getTotalSize(), test.getData().size());
        // if(test.getTotalSize() != test.getData().size()){
            // log.error("사이즈 다름: {}, {}", test.getTotalSize(), test.getData().size());
        // }

        // String recvJson = new String(receiveByte);
        // assemble.append(recvJson);
        // int index = assemble.indexOf(NetworkUtils.EOF);
        // if (index < 0) {
        //     return;
        // }

        
        // String targetSerialize = assemble.substring(0, index);
        // assemble.delete(0, index + NetworkUtils.EOF.length());

        // NetworkPacket NetworkPacket;
        // try {
        //     NetworkPacket = objectMapper.readValue(targetSerialize, NetworkPacket.class);
        // } catch (Exception e) {
        //     log.info("[TCP] 잘못된 수신 패킷 왔습니다.", e);
        //     log.error("[TCP] 잘못된 수신 패킷 왔습니다.", e);
        //     throw new UnsupportedOperationException(e);
        // }

        // outMsg.add(NetworkPacket);
    }

}
