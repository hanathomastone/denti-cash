package com.kaii.dentix.domain.admin.dto.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class OralCheckStatisticDto {

    private Long userId; // 사용자 고유번호

    private int oralCheckCount; // 구강검진 횟수

    private String oralCheckResultTotalType; // 구강검진 상태

    private String created; // 최근 구강 촬영 일시

}
