package org.gora.server.repository;

import org.gora.server.model.entity.RolePrivilegeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolePrivilegeRepository extends JpaRepository<RolePrivilegeEntity, Long> {
}
