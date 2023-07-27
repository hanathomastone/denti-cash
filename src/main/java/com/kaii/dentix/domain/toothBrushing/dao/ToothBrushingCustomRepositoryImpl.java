package com.kaii.dentix.domain.toothBrushing.dao;

import com.kaii.dentix.domain.toothBrushing.domain.QToothBrushing;
import com.kaii.dentix.domain.toothBrushing.dto.ToothBrushingDailyCountDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ToothBrushingCustomRepositoryImpl implements ToothBrushingCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final QToothBrushing toothBrushing = QToothBrushing.toothBrushing;

    @Override
    public List<ToothBrushingDailyCountDto> getDailyCount(Long userId) {

        return queryFactory
            .select(Projections.constructor(ToothBrushingDailyCountDto.class,
                Expressions.stringTemplate("DATE_FORMAT({0}, {1})", toothBrushing.created, "%Y-%m-%d"), Wildcard.count.intValue()
            ))
            .from(toothBrushing)
            .where(toothBrushing.userId.eq(userId))
            .groupBy(Expressions.stringTemplate("DATE_FORMAT({0}, {1})", toothBrushing.created, "%Y-%m-%d"))
            .fetch();
    }
}

