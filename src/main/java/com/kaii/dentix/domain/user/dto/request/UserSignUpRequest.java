package com.kaii.dentix.domain.user.dto.request;

import com.kaii.dentix.domain.type.GenderType;
import com.kaii.dentix.domain.userServiceAgreement.dto.request.UserServiceAgreementRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class UserSignUpRequest {

    @NotNull
    @Valid
    private List<UserServiceAgreementRequest>  userServiceAgreementRequest;

    @NotBlank @Size(min = 4, max = 12)
    private String userLoginId;

    @NotBlank @Size(min = 2, max = 6)
    private String userName;

    @NotNull
    private GenderType userGender;

    @NotBlank
    private String userBirth;

    @NotBlank @Size(min = 8, max = 20)
    private String userPassword;

    @NotNull
    private Long findPwdQuestionId;

    @NotBlank
    private String findPwdAnswer;

    private Long patientId;

    private String userDeviceModel;
    private String userDeviceManufacturer;
    private String userOsVersion;
    private String userDeviceToken;

}
