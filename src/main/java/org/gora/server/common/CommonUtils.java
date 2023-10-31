package org.gora.server.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

import io.jsonwebtoken.io.IOException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonUtils {
    public static final long SLEEP_MILLIS = 10;
 
    public static long bytesToMegabytes(long bytes) {
        return bytes / (1024 * 1024);
    }

    public static Object bytesToObject(byte[] bytes) throws IOException, ClassNotFoundException, java.io.IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return ois.readObject();
        }
    }
    public static byte[] objectToBytes(Object obj) throws IOException, java.io.IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            oos.flush();
            return bos.toByteArray();
        }
    }
    
    public static String replaceUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String getStackTraceElements(final Throwable e) {
        StackTraceElement[] stackElements;
        StringBuilder result = new StringBuilder();
        stackElements = e.getStackTrace();
        if (stackElements == null || stackElements.length == 0) {
            return null;
        }

        result.append(e);
        result.append('\n');
        for (StackTraceElement stackElement : stackElements) {
            result.append(stackElement);
            result.append('\n');
        }

        return result.toString();
    }

    public static void sleep(long millisecond){
        try {
            Thread.sleep(millisecond);
        } catch (InterruptedException e) {
            log.error("Thread sleep 에러");
            log.error(CommonUtils.getStackTraceElements(e));
            Thread.currentThread().interrupt();
        }
    }
}
