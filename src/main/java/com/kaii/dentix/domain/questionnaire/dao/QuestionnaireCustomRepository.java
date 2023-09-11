package com.kaii.dentix.domain.questionnaire.dao;

import com.kaii.dentix.domain.admin.dto.request.AdminStatisticRequest;
import com.kaii.dentix.domain.admin.dto.statistic.QuestionnaireStatisticDto;
import com.kaii.dentix.domain.questionnaire.dto.QuestionnaireAndStatusDto;

import java.util.List;

public interface QuestionnaireCustomRepository {

    QuestionnaireAndStatusDto getLatestQuestionnaireAndHigherStatus(Long userId);

    List<QuestionnaireStatisticDto> questionnaireList(AdminStatisticRequest request);

    int allQuestionnaireCount(AdminStatisticRequest request);

}

