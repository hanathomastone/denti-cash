package com.kaii.dentix.domain.admin.dto.statistic;

import com.kaii.dentix.domain.type.oral.OralCheckResultTotalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class OralCheckStatisticDto {

    private Long userId; // 사용자 고유번호

    private int oralCheckCount; // 구강검진 횟수

    private OralCheckResultTotalType oralCheckResultTotalType; // 구강검진 상태

    public OralCheckStatisticDto(Long userId, int oralCheckCount, String oralCheckResultTotalType) {
        this.userId = userId;
        this.oralCheckCount = oralCheckCount;
        this.oralCheckResultTotalType = OralCheckResultTotalType.valueOf(oralCheckResultTotalType);
    }

}
