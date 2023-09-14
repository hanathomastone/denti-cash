package com.kaii.dentix.domain.oralCheck.dao;

import com.kaii.dentix.domain.admin.dto.statistic.OralCheckResultTypeCount;
import com.kaii.dentix.domain.admin.dto.statistic.OralCheckUserCount;
import com.kaii.dentix.domain.admin.dto.request.AdminStatisticRequest;
import com.kaii.dentix.domain.oralCheck.domain.QOralCheck;
import com.kaii.dentix.domain.questionnaire.domain.QQuestionnaire;
import com.kaii.dentix.domain.type.oral.OralCheckResultTotalType;
import com.kaii.dentix.domain.user.domain.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class OralCheckCustomRepositoryImpl implements OralCheckCustomRepository {

    private final JPAQueryFactory queryFactory;

    private final QUser user = QUser.user;

    private final QOralCheck oralCheck = QOralCheck.oralCheck;

    private final QQuestionnaire questionnaire = QQuestionnaire.questionnaire;

    /**
     *  구강검진 결과 타입별 횟수
     */
    @Override
    public OralCheckResultTypeCount userOralCheckList(AdminStatisticRequest request){
        return queryFactory.select(Projections.constructor(OralCheckResultTypeCount.class,
                new CaseBuilder()
                        .when(oralCheck.oralCheckResultTotalType.eq(OralCheckResultTotalType.HEALTHY))
                        .then(1)
                        .otherwise(0)
                        .sum(),
                new CaseBuilder()
                        .when(oralCheck.oralCheckResultTotalType.eq(OralCheckResultTotalType.GOOD))
                        .then(1)
                        .otherwise(0)
                        .sum(),
                new CaseBuilder()
                        .when(oralCheck.oralCheckResultTotalType.eq(OralCheckResultTotalType.ATTENTION))
                        .then(1)
                        .otherwise(0)
                        .sum(),
                new CaseBuilder()
                        .when(oralCheck.oralCheckResultTotalType.eq(OralCheckResultTotalType.DANGER))
                        .then(1)
                        .otherwise(0)
                        .sum()
                ))
                .from(oralCheck)
                .join(user).on(oralCheck.userId.eq(user.userId))
                .where(
                        user.deleted.isNull(),
                        whereStartDate(request.getStartDate()),
                        whereEndDate(request.getEndDate())
                )
                .fetchOne();
    }

    /**
     *  구강검진을 한 총 사용자 수
     */
    @Override
    public Integer allUserOralCheckCount(AdminStatisticRequest request){
        return queryFactory.select(
                        user.userId.countDistinct().intValue()
                )
                .from(oralCheck)
                .join(user).on(oralCheck.userId.eq(user.userId))
                .where(
                        user.deleted.isNull(),
                        whereStartDate(request.getStartDate()),
                        whereEndDate(request.getEndDate())
                )
                .fetchOne();
    }

    /**
     *  기간 설정 '시작일' 필터링
     */
    private BooleanExpression whereStartDate(String date){
        return StringUtils.isNotBlank(date) ?
                Expressions.stringTemplate("DATE_FORMAT({0}, {1})", oralCheck.created, "%Y-%m-%d").goe(date).or
                        (Expressions.stringTemplate("DATE_FORMAT({0}, {1})", questionnaire.created, "%Y-%m-%d").goe(date))
                : null;
    }

    /**
     *  기간 설정 '종료일' 필터링
     */
    private BooleanExpression whereEndDate(String date){
        return StringUtils.isNotBlank(date) ?
                Expressions.stringTemplate("DATE_FORMAT({0}, {1})", oralCheck.created, "%Y-%m-%d").loe(date).or
                        (Expressions.stringTemplate("DATE_FORMAT({0}, {1})", questionnaire.created, "%Y-%m-%d").loe(date))
                : null;
    }

}
