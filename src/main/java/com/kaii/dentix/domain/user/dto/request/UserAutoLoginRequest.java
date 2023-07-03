package com.kaii.dentix.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserAutoLoginRequest {

    private String userDeviceModel;
    private String userDeviceManufacturer;
    private String userOsVersion;
    private String userDeviceToken;

}
