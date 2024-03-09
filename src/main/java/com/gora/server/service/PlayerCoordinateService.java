package com.gora.server.service;

import java.util.Date;

import com.gora.common.model.entity.TokenEntity;
import com.gora.common.model.entity.UserEntity;
import com.gora.common.model.entity.eTokenUseDBType;
import com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf;
import com.gora.common.repository.TokenRepository;
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
    public void broadcastPlayerCoordinate(String loginToken, PlayerCoordinateProtoBuf.PlayerCoordinate target){
        Date nowAt = new Date();
        TokenEntity tokenEntity = tokenRepository.findByAccessAndTypeAndAccessExpireAtAfter(loginToken, eTokenUseDBType.login, nowAt).orElse(null);
        if(tokenEntity == null){
            log.warn("만료된 토큰입니다.");
            return;
        }

        UserEntity userEntity = tokenEntity.getUser();
    }
}
