package com.kaii.dentix.domain.user.dto.request;

import com.kaii.dentix.domain.type.GenderType;
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
public class UserSignUpRequest {

    @NotNull
    @Valid
    private List<UserServiceAgreementRequest>  userServiceAgreementRequest;

    @NotBlank
    private String userLoginId;

    @NotBlank
    private String userName;

    @NotNull
    private GenderType userGender;

    @NotBlank
    private String userBirth;

    @NotBlank
    private String userPassword;

    @NotNull
    private Long findPwdQuestionId;

    @NotBlank
    private String findPwdAnswer;

    private Long patientId;

}
