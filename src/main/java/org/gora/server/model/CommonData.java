package org.gora.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gora.server.common.CommonUtils;

import java.io.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class CommonData implements Serializable{
    private Object data;
    private eCodeType type;
    private String key;
    @JsonIgnore
    private String senderIp;

    public static CommonData deserialization(byte[] target){
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(target);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

            objectInputStream.close();
            byteArrayInputStream.close();

            return (CommonData) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            log.error("객체 역직렬화 실패");
            log.error(CommonUtils.getStackTraceElements(e));
            return null;
        }
    }

    public static byte[] serialization(CommonData target){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(target);

            objectOutputStream.close();
            byteArrayOutputStream.close();
        } catch (IOException e) {
            log.error("객체 바이트 직렬화 실패");
            log.error(CommonUtils.getStackTraceElements(e));
            return null;
        }

        return byteArrayOutputStream.toByteArray();
    }
}
