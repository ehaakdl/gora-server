package org.gora.server.service;

import org.gora.server.common.AesUtils;
import org.gora.server.common.CommonUtils;
import org.gora.server.common.NetworkUtils;
import org.gora.server.component.network.ClientManager;
import org.gora.server.model.ClientConnection;
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

        log.info(transportData.getChanelId());
        NetworkPacket packet;

        clientManager.putResource(transportData.getChanelId(),
                ClientConnection.createUdp(new String(transportData.getData())));

        String encryptedChannelId = AesUtils.encrypt(transportData.getChanelId());
        log.info("{}", encryptedChannelId);

        packet = NetworkUtils.getEmptyData(eServiceType.udp_initial,
                encryptedChannelId);

        log.info("{}", packet.toByteArray().length);
        try {
            clientManager.send(eNetworkType.udp, eServiceType.udp_initial, packet,
                    transportData.getChanelId());
        } catch (Exception e) {
            log.error("udp initial 데이터 전송 실패 {}", CommonUtils.getStackTraceElements(e));
        }
    }

}
