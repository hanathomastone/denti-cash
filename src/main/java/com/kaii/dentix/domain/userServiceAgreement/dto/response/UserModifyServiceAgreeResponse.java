package com.kaii.dentix.domain.userServiceAgreement.dto.response;

import com.kaii.dentix.domain.userServiceAgreement.dto.UserModifyServiceAgreeDto;
import com.kaii.dentix.global.common.response.SuccessResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserModifyServiceAgreeResponse extends SuccessResponse {

    private UserModifyServiceAgreeDto userModifyServiceAgreeDto;

}
