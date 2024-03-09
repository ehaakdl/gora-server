package com.gora.server.component;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.gora.common.model.entity.TokenEntity;
import com.gora.common.model.entity.eTokenUseDBType;
import com.gora.common.repository.TokenRepository;
import com.gora.server.common.token.eTokenType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoginTokenProvider {
    private final TokenRepository tokenRepository;
    public boolean validToken(String token) {
        if (!eTokenType.validToken(token)) {
            return false;
        }

        TokenEntity tokenEntity = tokenRepository.findByAccessAndTypeAndAccessExpireAtAfter(
                token, eTokenUseDBType.login, new Date()).orElse(null);
        if (tokenEntity == null) {
            return false;
        }

        return true;
    }
}
