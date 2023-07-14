package org.gora.server.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonUtils {
    public static String getEnv(eEnv key, String defaultValue) {
        String result = System.getenv(key.name());
        return result == null ? defaultValue : result;
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
}
