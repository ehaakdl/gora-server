package org.gora.server.repository;

import org.gora.server.model.entity.QEmailVerifyEntity;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EmailVerifyCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    // 인증확인이 만료가 안된 이메일 체크
    public boolean existsEmailVerified(String email) {
        return jpaQueryFactory.select(QEmailVerifyEntity.emailVerifyEntity.verifiedExpireAt)
                .from(QEmailVerifyEntity.emailVerifyEntity)
                .where(
                    QEmailVerifyEntity.emailVerifyEntity.verifiedExpireAt.isNotNull(),
                    QEmailVerifyEntity.emailVerifyEntity.email.eq(email),
                    Expressions.currentTimestamp().before(QEmailVerifyEntity.emailVerifyEntity.verifiedExpireAt)
                ).limit(1)
                .fetchOne() != null;
    }
}
