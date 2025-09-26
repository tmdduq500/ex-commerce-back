package com.osy.commerce.user.repository;

import com.osy.commerce.user.domain.QUserAddress;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class UserAddressRepositoryImpl implements UserAddressRepositoryCustom {

    private final JPAQueryFactory query;

    public UserAddressRepositoryImpl(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public int clearDefaultByUserId(Long userId) {
        QUserAddress a = QUserAddress.userAddress;
        return (int) query.update(a)
                .set(a.isDefault, false)
                .where(a.user.id.eq(userId)
                        .and(a.isDefault.isTrue()))
                .execute();
    }
}
