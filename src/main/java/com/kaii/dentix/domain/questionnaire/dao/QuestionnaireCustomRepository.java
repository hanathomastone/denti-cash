package com.kaii.dentix.domain.questionnaire.dao;

import com.kaii.dentix.domain.questionnaire.dto.QuestionnaireAndStatusDto;

public interface QuestionnaireCustomRepository {

    QuestionnaireAndStatusDto getLatestQuestionnaireAndHigherStatus(Long userId);
}

