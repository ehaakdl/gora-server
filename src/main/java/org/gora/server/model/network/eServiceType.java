package org.gora.server.model.network;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum eServiceType {
    player_coordinate(1, true),
    test(2, true),
    health_check(3, true), chat(4, true), close_client(5, false), clean_data_buffer(6, false), udp_initial(7, true);

    private final int type;
    private final boolean isNetworkPacketUseType;

    public static eServiceType convertNetworkPacketServiceType(int type) {
        for (eServiceType value : eServiceType.values()) {
            if (value.type == type && value.isNetworkPacketUseType) {
                return value;
            }
        }

        return null;
    }
}
