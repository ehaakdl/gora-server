package org.gora.server.service;

import java.util.Date;

import org.gora.server.model.entity.TokenEntity;
import org.gora.server.model.entity.UserEntity;
import org.gora.server.model.entity.eTokenUseDBType;
import org.gora.server.model.network.PlayerCoordinate;
import org.gora.server.repository.TokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class PlayerCoordinateService {
    private final TokenRepository tokenRepository;

    @Transactional
    public void broadcastPlayerCoordinate(String loginToken, PlayerCoordinate target){
        Date nowAt = new Date();
        TokenEntity tokenEntity = tokenRepository.findByAccessAndTypeAndAccessExpireAtAfter(loginToken, eTokenUseDBType.login, nowAt).orElse(null);
        if(tokenEntity == null){
            log.warn("만료된 토큰입니다.");
            return;
        }

        UserEntity userEntity = tokenEntity.getUser();
    }
}
