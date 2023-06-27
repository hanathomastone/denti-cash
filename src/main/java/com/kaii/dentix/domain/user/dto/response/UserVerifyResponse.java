package com.kaii.dentix.domain.user.dto.response;

import com.kaii.dentix.domain.user.dto.UserVerifyDto;
import com.kaii.dentix.global.common.response.SuccessResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserVerifyResponse extends SuccessResponse {

    private UserVerifyDto userVerifyDto;

}
