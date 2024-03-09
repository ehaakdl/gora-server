package com.gora.server.service;

import com.gora.server.component.network.ClientManager;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ClientCloseService {

    public static void close(String channelId) {
        ClientManager.close(channelId);
    }

}
