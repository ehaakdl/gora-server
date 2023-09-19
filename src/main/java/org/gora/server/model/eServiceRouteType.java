package org.gora.server.model;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum eServiceRouteType {
    test(1);
    @JsonValue
    private final int type;
}
