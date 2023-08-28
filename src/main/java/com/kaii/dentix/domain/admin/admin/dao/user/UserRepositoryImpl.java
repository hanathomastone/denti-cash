package com.kaii.dentix.domain.admin.admin.dao.user;

import com.kaii.dentix.domain.admin.admin.dto.AdminUserInfoDto;
import com.kaii.dentix.domain.admin.admin.dto.request.AdminUserListRequest;
import com.kaii.dentix.domain.oralCheck.domain.QOralCheck;
import com.kaii.dentix.domain.questionnaire.domain.QQuestionnaire;
import com.kaii.dentix.domain.type.DatePeriodType;
import com.kaii.dentix.domain.type.oral.OralCheckResultTotalType;
import com.kaii.dentix.domain.user.domain.QUser;
import com.kaii.dentix.domain.userOralStatus.domain.QUserOralStatus;
import com.kaii.dentix.global.common.dto.PagingRequest;
import com.kaii.dentix.global.common.util.DateFormatUtil;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserCustomRepository{

    private final JPAQueryFactory queryFactory;

    private final QUser user = QUser.user;

    private final QOralCheck oralCheck = QOralCheck.oralCheck;

    private final QUserOralStatus userOralStatus = QUserOralStatus.userOralStatus;

    private final QQuestionnaire questionnaire = QQuestionnaire.questionnaire;

    /**
     *  사용자 목록 조회
     */
    @Override
    public Page<AdminUserInfoDto> findAll(AdminUserListRequest request){

        Pageable paging = new PagingRequest(request.getPage(), request.getSize()).of();

        // total 이 0보다 크면 조건에 맞게 페이징 처리 , 0 이면 빈 리스트 반환
        List<AdminUserInfoDto> result = queryFactory
                .select(Projections.constructor(AdminUserInfoDto.class,
                        user.userId, user.userLoginIdentifier, user.userName,
                        userOralStatus.oralStatus.oralStatusType, questionnaire.created.as("questionnaireDate"),
                        oralCheck.oralCheckResultTotalType, oralCheck.created.as("oralCheckDate"), user.isVerify
                ))
                .from(user)
                .leftJoin(questionnaire).on(questionnaire.userId.eq(user.userId)
                        .and(questionnaire.created.eq(JPAExpressions.select(questionnaire.created.max())
                                .from(questionnaire)
                                .where(questionnaire.userId.eq(user.userId))
                        )))
                .leftJoin(userOralStatus).on(questionnaire.questionnaireId.eq(userOralStatus.questionnaire.questionnaireId))
                .leftJoin(oralCheck).on(user.userId.eq(oralCheck.userId)
                        .and(oralCheck.created.eq(JPAExpressions.select(oralCheck.created.max())
                                .from(oralCheck)
                                .where(oralCheck.userId.eq(user.userId))
                        )))
                .where(
                        StringUtils.isNotBlank(request.getUserIdentifierOrName()) ?
                                user.userLoginIdentifier.contains(request.getUserIdentifierOrName()).or(user.userName.contains(request.getUserIdentifierOrName())) : null,
                        whereOralCheckResult(request.getOralCheckResultTotalType()),
                        request.getOralStatus() != null ? userOralStatus.oralStatus.oralStatusType.eq(request.getOralStatus()) : null,
                        request.getUserGender() != null ? user.userGender.eq(request.getUserGender()) : null,
                        request.getIsVerify() != null ? user.isVerify.eq(request.getIsVerify()) : null,
                        whereAllDatePeriod(request.getAllDatePeriod()),
                        whereStartDate(request.getStartDate()),
                        whereEndDate(request.getEndDate())
                )
                .orderBy(user.created.desc())
                .offset(paging.getOffset())
                .limit(paging.getPageSize())
                .fetch();

        // fetchCount Deprecated 로 인해 count 쿼리 구현
        Long total = Optional.ofNullable(queryFactory
                .select(user.count())
                .from(user)
                .leftJoin(questionnaire).on(questionnaire.userId.eq(user.userId)
                        .and(questionnaire.created.eq(JPAExpressions.select(questionnaire.created.max())
                                .from(questionnaire)
                                .where(questionnaire.userId.eq(user.userId))
                        )))
                .leftJoin(userOralStatus).on(questionnaire.questionnaireId.eq(userOralStatus.questionnaire.questionnaireId))
                .leftJoin(oralCheck).on(user.userId.eq(oralCheck.userId)
                        .and(oralCheck.created.eq(JPAExpressions.select(oralCheck.created.max())
                                .from(oralCheck)
                                .where(oralCheck.userId.eq(user.userId))
                        )))
                .where(
                        StringUtils.isNotBlank(request.getUserIdentifierOrName()) ?
                                user.userLoginIdentifier.contains(request.getUserIdentifierOrName()).or(user.userName.contains(request.getUserIdentifierOrName())) : null,
                        whereOralCheckResult(request.getOralCheckResultTotalType()),
                        request.getOralStatus() != null ? userOralStatus.oralStatus.oralStatusType.eq(request.getOralStatus()) : null,
                        request.getUserGender() != null ? user.userGender.eq(request.getUserGender()) : null,
                        request.getIsVerify() != null ? user.isVerify.eq(request.getIsVerify()) : null,
                        whereAllDatePeriod(request.getAllDatePeriod()),
                        whereStartDate(request.getStartDate()),
                        whereEndDate(request.getEndDate())
                )
                .fetchOne()).orElse(0L);

        return new PageImpl<>(result, paging, total);

    }

    /**
     *  구강 상태 필터링
     */
    private BooleanExpression whereOralCheckResult(OralCheckResultTotalType oralCheckResultTotalType){
        return oralCheckResultTotalType == null ? null : oralCheck.oralCheckResultTotalType.eq(oralCheckResultTotalType);
    }

    /**
     *  기간 설정 타입 필터링 (구강 촬영일 or 문진표 검사일)
     */
    private BooleanExpression whereAllDatePeriod(DatePeriodType type){

        if (type != null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 1);

            // 내일 00시 00분 기준으로 시작
            switch (type) {
                case TODAY: cal.add(Calendar.DATE, -1); break;
                case WEEK1: cal.add(Calendar.DATE, -7); break;
                case MONTH1: cal.add(Calendar.MONTH, -1); break;
                case MONTH3: cal.add(Calendar.MONTH, -3); break;
                case YEAR1: cal.add(Calendar.YEAR, -1); break;
                case ALL: return null;
            }

            Date startDate = cal.getTime();

            return Expressions.stringTemplate("DATE_FORMAT({0}, {1})", oralCheck.created, "%Y-%m-%d").goe(DateFormatUtil.dateToString("yyyy-MM-dd", startDate)).or
                    (Expressions.stringTemplate("DATE_FORMAT({0}, {1})", questionnaire.created, "%Y-%m-%d").goe(DateFormatUtil.dateToString("yyyy-MM-dd", startDate)));
        }
        return null;
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
