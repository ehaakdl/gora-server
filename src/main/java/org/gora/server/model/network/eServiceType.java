package org.gora.server.model.network;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum eServiceType {
    player_coordinate(1)
    , test(2)
    , health_check(3)
    // 단일 스레드인 로직 스레드에서 자원을 해제 시키기 위해 일부러 타입을 추가함 패킷을 디코딩하고 읽어들이는 워커 스레드가 해당 타입사용한다. 
    , close_client(4)
    ;
    private final int type;

    public static eServiceType convert(int type){
        for (eServiceType value : values()) {
            if(value.getType() == type){
                return value;
            }
        }
        
        return null;
    }
}
