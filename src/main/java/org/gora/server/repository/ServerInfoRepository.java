package org.gora.server.repository;

import org.gora.server.model.entity.ServerInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerInfoRepository extends JpaRepository<ServerInfoEntity, Long> {
}
