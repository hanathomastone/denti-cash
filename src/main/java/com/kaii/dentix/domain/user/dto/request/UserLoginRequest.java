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

    @NotBlank @Size(min = 4, max = 12)
    private String userLoginIdentifier;

    @NotBlank @Size(min = 8, max = 20)
    private String userPassword;

    private String userDeviceModel;
    private String userDeviceManufacturer;
    private String userOsVersion;
    private String userDeviceToken;

}
