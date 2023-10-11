package org.gora.server.model;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 네트워크로 전송된 패킷에 서비스 유형 나타냄
@RequiredArgsConstructor
@Getter
public enum eServiceRouteType {
    player_coordinate(1),
    health_check(2);
    @JsonValue
    private final int type;

    public static eServiceRouteType convert(int serviceRouteTypetype){
        for (eServiceRouteType values : eServiceRouteType.values()) {
            if(serviceRouteTypetype == values.type){
                return values;
            }
        }
        
        return null;
    }
}
