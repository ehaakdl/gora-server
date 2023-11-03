package org.gora.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class QueryDslConfig {

   @PersistenceContext
   private EntityManager entityManager;

   @Bean
   public JPAQueryFactory jpaQueryFactory() {
    log.info("open?? :{}", entityManager.isOpen());
       return new JPAQueryFactory(entityManager);
   }
}