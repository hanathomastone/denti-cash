package com.kaii.dentix.domain.findPwdQuestion.dao;

import com.kaii.dentix.domain.findPwdQuestion.domain.FindPwdQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FindPwdQuestionRepository extends JpaRepository<FindPwdQuestion, Long> {
}
