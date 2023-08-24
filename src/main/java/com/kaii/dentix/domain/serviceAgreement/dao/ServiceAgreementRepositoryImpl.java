package com.kaii.dentix.domain.serviceAgreement.dao;

import com.kaii.dentix.domain.serviceAgreement.domain.QServiceAgreement;
import com.kaii.dentix.domain.type.YnType;
import com.kaii.dentix.domain.userServiceAgreement.domain.QUserServiceAgreement;
import com.kaii.dentix.domain.userServiceAgreement.dto.UserServiceAgreeList;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ServiceAgreementRepositoryImpl implements ServiceAgreementCustomRepository{

    private final JPAQueryFactory queryFactory;

    private final QServiceAgreement serviceAgreement = QServiceAgreement.serviceAgreement;

    private final QUserServiceAgreement userServiceAgreement = QUserServiceAgreement.userServiceAgreement;

    /**
     *  사용자 서비스 이용 동의 '선택' 항목 리스트 조회
     */
    @Override
    public List<UserServiceAgreeList> findAllByNotRequiredServiceAgreement(Long userId){
        return queryFactory
                .select(Projections.constructor(UserServiceAgreeList.class,
                        serviceAgreement.serviceAgreeId,
                        new CaseBuilder()
                                .when(userServiceAgreement.isUserServiceAgree.isNull())
                                .then(YnType.N.toString())
                                .otherwise(userServiceAgreement.isUserServiceAgree.stringValue()).as("isUserServiceAgree"),
                        userServiceAgreement.userServiceAgreeDate.as("date")
                ))
                .from(serviceAgreement)
                .leftJoin(userServiceAgreement).on(userServiceAgreement.serviceAgreeId.eq(serviceAgreement.serviceAgreeId)
                        .and(userServiceAgreement.userId.eq(userId)))
                .where(serviceAgreement.isServiceAgreeRequired.eq(YnType.N))
                .orderBy(serviceAgreement.serviceAgreeSort.asc())
                .fetch();
    }

}
