package org.gora.server.component;

import java.util.ArrayList;
import java.util.List;

import org.gora.server.component.network.ClientManager;
import org.gora.server.model.ClientConnection;
import org.gora.server.model.eProtocol;
import org.gora.server.model.eServiceRouteType;
import org.gora.server.model.network.NetworkPacket;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class HealthCheckingManager {
    private final ClientManager clientManager;
    private final static List<String> cleanTargetClients = new ArrayList<>();

    public void removeCleanTargetClient(String key){
        
    }
    @Scheduled(fixedDelay = 1000 * 60 * 5)
    public void cleanSession() {
        for (String key : cleanTargetClients) {
            clientManager.remove(key);
        }

        List<String> keys = clientManager.getAllKeys();
        boolean isSuccessSend = true;
        for (String key : keys) {
            ClientConnection clientConnection = clientManager.get(key);
            NetworkPacket pakcet = new NetworkPacket(null, eServiceRouteType.health_check, key);
            if (clientConnection.isConnectionTcp()) {
                pakcet.setProtocol(eProtocol.tcp);
                isSuccessSend = clientManager.send(pakcet);
            } else {
                pakcet.setProtocol(eProtocol.udp);
                isSuccessSend = clientManager.send(pakcet);
            }

            if (isSuccessSend) {
                cleanTargetClients.add(key);
            } else {
                clientManager.remove(key);
            }
        }
    }
}