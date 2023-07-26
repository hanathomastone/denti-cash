package com.kaii.dentix.domain.questionnaire.dao;

import com.kaii.dentix.domain.questionnaire.domain.Questionnaire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionnaireRepository extends JpaRepository<Questionnaire, Long> {

    List<Questionnaire> findAllByUserIdOrderByCreatedDesc(Long userId);
}
