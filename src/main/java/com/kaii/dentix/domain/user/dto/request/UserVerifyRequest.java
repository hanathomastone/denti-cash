package com.kaii.dentix.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class UserVerifyRequest {

    @NotBlank @Size(min = 11)
    private String patientPhoneNumber;

    @NotBlank @Size(min = 2)
    private String patientName;

}
