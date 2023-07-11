package com.kaii.dentix.global.common.aws.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NcsStsRequestDTO {

    private int durationSec; // accessKey 지속시간(초), default: 43200, min: 900, max: 129600

}
