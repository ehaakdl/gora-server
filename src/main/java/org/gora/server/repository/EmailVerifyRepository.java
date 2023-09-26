package org.gora.server.repository;

import java.util.List;

import org.gora.server.model.entity.EmailVerifyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerifyRepository extends JpaRepository<EmailVerifyEntity, Long> {
    List<EmailVerifyEntity> findAllByEmail(String email);
}
