package org.gora.server.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum eRouteServiceType {
    player_coordinate(1), test(2), health_check(3), chat_msg_from_client(4), chat_msg_from_server(5), close_client(6);

    private final int type;

    public static eRouteServiceType convert(int type) {
        for (eRouteServiceType value : values()) {
            if (value.getType() == type) {
                return value;
            }
        }

        return null;
    }
}
