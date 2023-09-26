package org.gora.server.repository;

import org.gora.server.model.entity.SocialUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialUserRepository extends JpaRepository<SocialUserEntity, Long> {
}
