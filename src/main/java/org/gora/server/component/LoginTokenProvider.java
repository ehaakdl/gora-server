package org.gora.server.component;

import java.util.Date;

import org.gora.server.common.token.TokenUtils;
import org.gora.server.model.entity.TokenEntity;
import org.gora.server.model.entity.eTokenUseDBType;
import org.gora.server.repository.TokenRepository;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoginTokenProvider {
    private final TokenRepository tokenRepository;
    private final TokenUtils tokenUtils;

    public boolean validToken(String token) {
        if (!tokenUtils.validToken(token)) {
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
