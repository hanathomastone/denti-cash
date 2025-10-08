package com.kaii.dentix.domain.user.dto;

import com.kaii.dentix.domain.type.GenderType;
import com.kaii.dentix.domain.userServiceAgreement.dto.UserServiceAgreeList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
@AllArgsConstructor
public class UserInfoDto {

    private String userName;

    private String userLoginIdentifier;

    private String patientPhoneNumber;

    private List<UserServiceAgreeList> userServiceAgreeLists;

    private GenderType userGender;

    private Long tokenBalance;

}
