package com.kaii.dentix.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class UserVerifyRequest {

    @NotBlank
    @Pattern(regexp = "^[0-9]{10,11}$")
    private String patientPhoneNumber;

    @NotBlank
    @Pattern(regexp = "^[가-힣a-zA-Z\s]{2,100}$")
    private String patientName;

}
