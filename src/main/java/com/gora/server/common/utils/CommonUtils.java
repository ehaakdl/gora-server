package com.gora.server.common.utils;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonUtils {
    public static final long SLEEP_MILLIS = 10;

    public static boolean isInstance(Object target, Class<?> type){
        return type.isInstance(target);
    }

    public static long bytesToMegabytes(long bytes) {
        return bytes / (1024 * 1024);
    }

    public static String replaceUUID() {
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

    public static void sleep(long millisecond) {
        try {
            Thread.sleep(millisecond);
        } catch (InterruptedException e) {
            log.error("Thread sleep 에러");
            log.error(CommonUtils.getStackTraceElements(e));
            Thread.currentThread().interrupt();
        }
    }
}
