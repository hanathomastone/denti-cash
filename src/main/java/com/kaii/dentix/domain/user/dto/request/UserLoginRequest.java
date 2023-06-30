package com.kaii.dentix.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class UserLoginRequest {

    @NotBlank
    private String userLoginId;

    @NotBlank
    private String userPassword;

}
