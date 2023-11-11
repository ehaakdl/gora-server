package org.gora.server.model.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ExpiredPacketException extends RuntimeException {
    public ExpiredPacketException(String clientResourceKey, Throwable cause) {
        super(cause);
        this.clientResourceKey = clientResourceKey;
    }

    private final String clientResourceKey;
}
