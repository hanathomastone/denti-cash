package com.kaii.dentix.domain.oralCheck.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class OralCheckResultCount {

    private int countHealthy;

    private int countGood;

    private int countAttention;

    private int countDanger;

}
