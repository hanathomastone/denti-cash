package com.kaii.dentix.domain.oralCheck.dto.resoponse;

import com.kaii.dentix.domain.oralCheck.dto.OralCheckResultDto;
import com.kaii.dentix.global.common.response.SuccessResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class OralCheckResultResponse extends SuccessResponse {

    private OralCheckResultDto oralCheckResultDto;

}
