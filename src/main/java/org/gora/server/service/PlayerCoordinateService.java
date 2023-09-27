package org.gora.server.service;

import org.gora.server.model.network.PlayerCoordinate;
import org.gora.server.repository.TokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PlayerCoordinateService {
    private final TokenRepository tokenRepository;

    @Transactional
    public void broadcasePlayerCoordinate(String loginToken, PlayerCoordinate target){
        
    }
}
