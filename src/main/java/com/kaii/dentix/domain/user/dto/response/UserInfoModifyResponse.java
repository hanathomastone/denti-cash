package com.kaii.dentix.domain.user.dto.response;

import com.kaii.dentix.domain.user.dto.UserInfoModifyDto;
import com.kaii.dentix.global.common.response.SuccessResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoModifyResponse extends SuccessResponse {

    private UserInfoModifyDto userInfoModifyDto;

}
