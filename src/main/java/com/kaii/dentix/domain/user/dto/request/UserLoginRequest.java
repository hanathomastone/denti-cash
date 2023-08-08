package com.kaii.dentix.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class UserLoginRequest {

    @NotBlank(message = "아이디는 필수입니다.")
    private String userLoginIdentifier;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String userPassword;

    private String userDeviceModel;
    private String userDeviceManufacturer;
    private String userOsVersion;
    private String userDeviceToken;

}
