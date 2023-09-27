package org.gora.server.model;

import java.io.IOException;
import java.io.Serializable;

import org.gora.server.common.NetworkUtils;
import org.gora.server.component.JsonCommonDataDeserialize;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@JsonDeserialize(using = JsonCommonDataDeserialize.class)
public class CommonData implements Serializable{
    private Object data;
    private eServiceRouteType type;
    @JsonIgnore
    private eProtocol protocol;
    private String key;

    public CommonData(Object data, eServiceRouteType type, String key){
        this.type = type;
        this.data = data;
        this.key = key;
    }

    public CommonData(Object data, eServiceRouteType type){
        this.type = type;
        this.data = data;
    }
    
    public static CommonData convert(ByteBuf buf, ObjectMapper objectMapper) throws StreamReadException, DatabindException, IOException{
        ByteBuf byteBuf = buf;
        byte[] receiveByte = new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(byteBuf.readerIndex(), receiveByte);
        return objectMapper.readValue(receiveByte, CommonData.class);
    }

    public static ByteBuf convertByteBuf(CommonData data, ObjectMapper objectMapper){
        byte[] message;
        try {
            String json = objectMapper.writeValueAsString(data) + NetworkUtils.EOF;
            message = json.getBytes();
        } catch (JsonProcessingException e) {
            return null;
        }
        
        return Unpooled.wrappedBuffer(message);
    }
}
