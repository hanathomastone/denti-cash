package com.kaii.dentix.domain.userServiceAgreement.dto;

import com.kaii.dentix.domain.type.YnType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
@AllArgsConstructor
public class UserModifyServiceAgreeDto {

    private YnType isUserServiceAgree;

}
