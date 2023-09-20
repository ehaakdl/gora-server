package org.gora.server.model;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 네트워크로 전송된 패킷에 서비스 유형 나타냄
@RequiredArgsConstructor
@Getter
public enum eServiceRouteType {
    test(1);
    @JsonValue
    private final int type;
}
