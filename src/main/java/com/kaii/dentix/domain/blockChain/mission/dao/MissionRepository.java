package com.kaii.dentix.domain.blockChain.mission.dao;

import com.kaii.dentix.domain.blockChain.mission.domain.Mission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface MissionRepository extends JpaRepository<Mission, Long> {

    /**JPQL 기반 검색 + 페이징 */
    @Query("""
        SELECT m FROM Mission m
        WHERE (:keyword IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:active IS NULL OR m.active = :active)
          AND (:startDate IS NULL OR m.startDate >= :startDate)
          AND (:endDate IS NULL OR m.endDate <= :endDate)
        ORDER BY m.created DESC
    """)
    Page<Mission> searchMissions(
            @Param("keyword") String keyword,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("active") Boolean active,
            Pageable pageable
    );
}