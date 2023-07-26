package com.kaii.dentix.domain.questionnaire.dao;

import com.kaii.dentix.domain.questionnaire.domain.Questionnaire;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionnaireRepository extends JpaRepository<Questionnaire, Long> {
}
