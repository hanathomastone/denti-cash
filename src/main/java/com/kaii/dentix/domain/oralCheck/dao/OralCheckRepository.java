package com.kaii.dentix.domain.oralCheck.dao;

import com.kaii.dentix.domain.oralCheck.domain.OralCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OralCheckRepository extends JpaRepository<OralCheck, Long> {

    List<OralCheck> findAllByUserIdOrderByCreatedDesc(Long userId);

    @Query(value = "SELECT a FROM OralCheck a WHERE a.oralCheckAnalysisState = 'SUCCESS' AND a.userId = :userId AND FUNCTION('DATE_FORMAT', a.created, '%Y-%m-%d') = :created ORDER BY a.created")
    List<OralCheck> findByUserIdAndCreated(Long userId, String created);

}