package com.kaii.dentix.domain.admin.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class AdminSignUpRequest {

    @NotBlank
    private String adminName;

    @NotBlank
    private String adminLoginIdentifier;

    @NotBlank
    private String adminPhoneNumber;

}
