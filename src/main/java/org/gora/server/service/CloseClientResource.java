package org.gora.server.service;

import org.gora.server.component.network.ClientManager;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CloseClientResource {
    private final ClientManager clientManager;

    public void close(String channelId) {
        clientManager.close(channelId);
    }

}
