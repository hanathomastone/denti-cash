package com.kaii.dentix.domain.questionnaire.dao;

import com.kaii.dentix.domain.oralStatus.domain.QOralStatus;
import com.kaii.dentix.domain.questionnaire.domain.QQuestionnaire;
import com.kaii.dentix.domain.questionnaire.dto.QuestionnaireAndStatusDto;
import com.kaii.dentix.domain.userOralStatus.domain.QUserOralStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QuestionnaireCustomRepositoryImpl implements QuestionnaireCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final QQuestionnaire questionnaire = QQuestionnaire.questionnaire;
    private final QOralStatus oralStatus = QOralStatus.oralStatus;
    private final QUserOralStatus userOralStatus = QUserOralStatus.userOralStatus;

    /**
     * 최근 문진표 및 해당 문진표의 높은 우선순위 구강 상태 조회
     */
    @Override
    public QuestionnaireAndStatusDto getLatestQuestionnaireAndHigherStatus(Long userId) {

        return queryFactory
            .select(Projections.constructor(QuestionnaireAndStatusDto.class,
                questionnaire.questionnaireId, questionnaire.created, oralStatus.oralStatusType, oralStatus.oralStatusTitle
            ))
            .from(questionnaire)
            .join(userOralStatus).on(userOralStatus.questionnaire.questionnaireId.eq(questionnaire.questionnaireId))
            .join(oralStatus).on(oralStatus.oralStatusType.eq(userOralStatus.oralStatus.oralStatusType))
            .where(questionnaire.userId.eq(userId))
            .orderBy(questionnaire.created.desc(), oralStatus.oralStatusPriority.asc())
            .fetchFirst();
    }
}

