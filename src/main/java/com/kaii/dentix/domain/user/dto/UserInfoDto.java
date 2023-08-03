package com.kaii.dentix.domain.user.dto;

import com.kaii.dentix.domain.type.YnType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
@AllArgsConstructor
public class UserInfoDto {

    private String userName;

    private String userLoginIdentifier;

    private String patientPhoneNumber;

    private YnType isUserMarketingAgree;

}
