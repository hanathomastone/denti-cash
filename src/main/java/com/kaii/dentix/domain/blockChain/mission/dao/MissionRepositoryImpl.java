package com.kaii.dentix.domain.blockChain.mission.dao;


import com.kaii.dentix.domain.blockChain.mission.domain.Mission;
import com.kaii.dentix.domain.blockChain.mission.domain.QMission;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
@RequiredArgsConstructor
public class MissionRepositoryImpl implements MissionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Mission> searchMissions(String keyword, LocalDate startDate, LocalDate endDate, Boolean active) {
        QMission m = QMission.mission;

        return queryFactory.selectFrom(m)
                .where(
                        keyword != null ? m.name.containsIgnoreCase(keyword) : null,
                        startDate != null ? m.startDate.goe(startDate) : null,
                        endDate != null ? m.endDate.loe(endDate) : null,
                        active != null ? m.active.eq(active) : null
                )
                .orderBy(m.created.desc())
                .fetch();
    }
}