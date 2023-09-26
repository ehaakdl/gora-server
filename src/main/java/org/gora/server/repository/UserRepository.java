package org.gora.server.repository;

import java.util.Optional;

import org.gora.server.model.entity.UserEntity;
import org.gora.server.model.entity.eUserType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmailAndType(String email, eUserType userType);
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    
}
