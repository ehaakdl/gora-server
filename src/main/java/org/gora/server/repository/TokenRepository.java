package org.gora.server.repository;

import java.util.Date;
import java.util.Optional;

import org.gora.server.model.entity.TokenEntity;
import org.gora.server.model.entity.eTokenUseDBType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<TokenEntity, Long> {
    Optional<TokenEntity> findByAccess(String accessToken);
    Optional<TokenEntity> findByAccessAndTypeAndAccessExpireAtAfter(String accessToken, eTokenUseDBType type, Date accessExpireAt);
    void deleteByAccess(String accessToken);
}
