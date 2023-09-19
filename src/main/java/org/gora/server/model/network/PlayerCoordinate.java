package org.gora.server.model.network;

import java.io.IOException;
import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class PlayerCoordinate implements Serializable{
    private float x;
    private float y;
    
    public static PlayerCoordinate convert(ByteBuf buf, ObjectMapper objectMapper) throws StreamReadException, DatabindException, IOException{
        ByteBuf byteBuf = buf;
        byte[] receiveByte = new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(byteBuf.readerIndex(), receiveByte);
        return objectMapper.readValue(receiveByte, PlayerCoordinate.class);
    }

    public static ByteBuf convertByteBuf(PlayerCoordinate data, ObjectMapper objectMapper){
        byte[] message;
        try {
            message = objectMapper.writeValueAsString(data).getBytes();
        } catch (JsonProcessingException e) {
            return null;
        }
        
        return Unpooled.wrappedBuffer(message);
    }
}
