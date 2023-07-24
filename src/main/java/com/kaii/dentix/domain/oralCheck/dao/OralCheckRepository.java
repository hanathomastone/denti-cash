package com.kaii.dentix.domain.oralCheck.dao;

import com.kaii.dentix.domain.oralCheck.domain.OralCheck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OralCheckRepository extends JpaRepository<OralCheck, Long> {

    List<OralCheck> findAllByUserIdOrderByCreatedDesc(Long userId);

}