package org.gora.server.component;

import java.util.List;
import java.util.Set;

import org.gora.server.component.network.ClientManager;
import org.gora.server.model.network.ClientNetworkBuffer;
import org.gora.server.model.network.ClientNetworkDataWrapper;
import org.gora.server.model.network.ClientResource;
import org.gora.server.model.network.eNetworkType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CleanResource {
    private final ClientManager clientManager;
    @Value("${app.clientDataBufferExpireTime}")
    private long dataBufferExpireTime;

    private void dataBufferCleanLoop(long nowAt, eNetworkType type, ClientNetworkBuffer networkBuffer) {
        List<String> identifyList;
        if (type == eNetworkType.tcp) {
            identifyList = networkBuffer.getTcpIdentifyList();
        } else {
            identifyList = networkBuffer.getUdpIdentifyList();
        }
        
        for (int index = 0; index < identifyList.size(); index++) {
            String identify;
            try {
                identify = identifyList.get(0);
                networkBuffer.removeDataWrapper(identify, type);
            } catch (Exception e) {
                return;
            }

            ClientNetworkDataWrapper dataWrapper = networkBuffer.getDataWrapper(identify, type);
            if (dataWrapper == null) {
                continue;
            }

            long expiredAt = +dataWrapper.getAppendAt() + dataBufferExpireTime;
            if (nowAt >= expiredAt) {
                networkBuffer.removeDataWrapper(identify, type);
            }

        }
    }

    @Scheduled(fixedDelayString = "${app.clientDataBufferCleanDelay}")
    public void clientDataBufferClean() {

        Set<String> keys = clientManager.getResourceKeys();
        long nowAt = System.currentTimeMillis();
        while (keys.iterator().hasNext()) {
            String resourceKey = keys.iterator().next();
            ClientResource clientResource = clientManager.getResource(resourceKey);
            if (clientResource == null) {
                continue;
            }

            ClientNetworkBuffer networkBuffer = clientResource.getBuffer();

            // tcp data 버퍼 만료 루프
            dataBufferCleanLoop(nowAt, eNetworkType.tcp, networkBuffer);
            // udp data 버퍼 만료 루프
            dataBufferCleanLoop(nowAt, eNetworkType.udp, networkBuffer);
        }
    }

}
