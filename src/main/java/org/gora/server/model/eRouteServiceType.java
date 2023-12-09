package org.gora.server.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum eRouteServiceType {
    player_coordinate(1), test(2), health_check(3), chat(4), close_client(5), clean_data_buffer(6), udp_initial(7);

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
