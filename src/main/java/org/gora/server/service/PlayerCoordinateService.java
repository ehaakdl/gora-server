package org.gora.server.service;

import org.gora.server.model.network.PlayerCoordinate;
import org.gora.server.repository.TokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class PlayerCoordinateService {
    private final TokenRepository tokenRepository;

    @Transactional
    public void broadcastPlayerCoordinate(String loginToken, PlayerCoordinate target){
        
    }
}
