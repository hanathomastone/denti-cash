package com.kaii.dentix.domain.admin.dto.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class OralCheckUserCount {

    private int oralCheckUserCount; // 구강검진을 한 사용자 수

}
