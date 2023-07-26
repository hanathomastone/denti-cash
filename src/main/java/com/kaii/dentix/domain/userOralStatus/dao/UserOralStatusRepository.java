package com.kaii.dentix.domain.userOralStatus.dao;

import com.kaii.dentix.domain.questionnaire.domain.Questionnaire;
import com.kaii.dentix.domain.userOralStatus.domain.UserOralStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserOralStatusRepository extends JpaRepository<UserOralStatus, Long> {

    List<UserOralStatus> findAllByQuestionnaireIn(List<Questionnaire> questionnaireList);
}
