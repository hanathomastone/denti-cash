package com.kaii.dentix.domain.user.dto;

import com.kaii.dentix.domain.type.GenderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
@AllArgsConstructor
public class UserInfoModifyDto {

    private String userName;

    private GenderType userGender;

    private String userBirth;

}
