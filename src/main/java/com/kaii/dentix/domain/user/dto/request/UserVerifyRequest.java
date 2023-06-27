package com.kaii.dentix.domain.user.dto.request;

import com.kaii.dentix.domain.userServiceAgreement.dto.request.UserServiceAgreementRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor @NoArgsConstructor
public class UserVerifyRequest {

    @NotBlank
    private String patientPhoneNumber;

    @NotBlank
    private String patientName;

    @NotNull
    @Valid
    private List<UserServiceAgreementRequest> userServiceAgreementRequest;

}
