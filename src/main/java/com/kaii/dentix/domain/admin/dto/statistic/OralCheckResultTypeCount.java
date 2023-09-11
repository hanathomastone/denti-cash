package com.kaii.dentix.domain.admin.dto.statistic;

import lombok.*;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class OralCheckResultTypeCount {

    private int countHealthy;

    private int countGood;

    private int countAttention;

    private int countDanger;

}
