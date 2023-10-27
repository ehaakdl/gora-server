package org.gora.server.model.network;

import java.io.IOException;
import java.io.Serializable;

import org.gora.server.component.JsonNetworkPacketDeserialize;
import org.gora.server.model.eProtocol;
import org.gora.server.model.eServiceRouteType;

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
@JsonDeserialize(using = JsonNetworkPacketDeserialize.class)
public class NetworkPacket implements Serializable{
    private Object data;
    private eServiceRouteType type;
    @JsonIgnore
    private eProtocol protocol;
    private String key;

    public NetworkPacket(Object data, eServiceRouteType type, String key){
        this.type = type;
        this.data = data;
        this.key = key;
    }

    public NetworkPacket(Object data, eServiceRouteType type){
        this.type = type;
        this.data = data;
    }


    public NetworkPacket(Object data, eServiceRouteType type, String key, eProtocol protocol){
        this.type = type;
        this.data = data;
        this.key = key;
        this.protocol = protocol;
    }
    
    public static NetworkPacket convertFromNetworkPacket(ByteBuf buf, ObjectMapper objectMapper) throws StreamReadException, DatabindException, IOException{
        ByteBuf byteBuf = buf;
        byte[] receiveByte = new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(byteBuf.readerIndex(), receiveByte);
        return objectMapper.readValue(receiveByte, NetworkPacket.class);
    }

    public static ByteBuf convertByteBuf(NetworkPacket data, ObjectMapper objectMapper){
        byte[] message;
        try {
            String json = objectMapper.writeValueAsString(data);
            message = json.getBytes();
        } catch (JsonProcessingException e) {
            return null;
        }
        
        return Unpooled.wrappedBuffer(message);
    }
}
