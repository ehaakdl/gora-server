package org.gora.server.common;

import org.gora.server.model.CommonData;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NetworkUtils {
    public static final String EOF_STRING = "@@@";
    private final ObjectMapper objectMapper;

    public CommonData decode(StringBuilder assemble, ByteBuf content) throws JsonMappingException, JsonProcessingException {
        
        byte[] contentByte = new byte[content.readableBytes()];
        content.readBytes(contentByte);
       
        String contentJson = new String(contentByte);
        assemble.append(contentJson);
        int index = assemble.indexOf(EOF_STRING);
        if (index < 0) {
            return null;
        }

        String targetSerialize = assemble.substring(0, index);
        assemble.delete(0, index + EOF_STRING.length());

        return objectMapper.readValue(targetSerialize, CommonData.class);
    }    
}
