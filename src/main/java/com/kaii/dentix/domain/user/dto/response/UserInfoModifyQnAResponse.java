package com.kaii.dentix.domain.user.dto.response;

import com.kaii.dentix.domain.user.dto.UserInfoModifyQnADto;
import com.kaii.dentix.global.common.response.SuccessResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoModifyQnAResponse extends SuccessResponse {

    private UserInfoModifyQnADto userInfoModifyQnADto;

}
