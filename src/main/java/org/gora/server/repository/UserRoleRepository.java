package org.gora.server.repository;

import java.util.List;

import org.gora.server.model.entity.RoleEntity;
import org.gora.server.model.entity.UserEntity;
import org.gora.server.model.entity.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long> {
    boolean existsByUserAndRole(UserEntity user, RoleEntity role);
    List<UserRoleEntity> findAllByUser(UserEntity user);
}
