package com.kaii.dentix.domain.admin.dto.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class QuestionnaireStatisticDto {

    private Long userId; // 사용자 고유번호

    private String questionnaireType; // 문진표 결과 타입

}
