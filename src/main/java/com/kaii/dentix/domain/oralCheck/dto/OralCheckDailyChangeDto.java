package com.kaii.dentix.domain.oralCheck.dto;

import com.kaii.dentix.domain.type.oral.OralCheckResultType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class OralCheckDailyChangeDto {

    private int oralCheckNumber;
    private OralCheckResultType oralCheckResultTotalType;
}
