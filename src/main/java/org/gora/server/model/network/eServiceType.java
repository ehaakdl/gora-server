package org.gora.server.model.network;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum eServiceType {
    player_coordinate(1), test(2), health_check(3);
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
