package com.kaii.dentix.domain.admin.dto.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class AllQuestionnaireCount {

    private int allQuestionnaireCount; // 전체 문진표 작성 횟수

}
