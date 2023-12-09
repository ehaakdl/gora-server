package org.gora.server.service;

import org.gora.server.common.CommonUtils;
import org.gora.server.common.NetworkUtils;
import org.gora.server.component.network.ClientManager;
import org.gora.server.model.TransportData;
import org.gora.server.model.network.NetworkPackcetProtoBuf.NetworkPacket;
import org.gora.server.model.network.eNetworkType;
import org.gora.server.model.network.eServiceType;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class ClientService {
    private final ClientManager clientManager;

    public void initialUdp(TransportData transportData) {
        eServiceType serviceType = eServiceType.udp_initial;
        NetworkPacket packet = NetworkUtils.getPacket(transportData.getData(), serviceType);
        try {
            if (!clientManager.send(eNetworkType.udp, serviceType, packet,
                    transportData.getChanelId())) {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            log.error("udp initial 데이터 전송 실패 {}", CommonUtils.getStackTraceElements(e));
        }
    }

}
