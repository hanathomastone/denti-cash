package com.kaii.dentix.domain.admin.dto;

import com.kaii.dentix.domain.type.GenderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class AdminUserModifyInfoDto {

    private String userLoginIdentifier; // 사용자 아이디
    private String userName; // 사용자 이름
    private GenderType userGender; // 사용자 성별
}
