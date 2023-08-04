package com.kaii.dentix.domain.user.dto.request;

import com.kaii.dentix.domain.type.GenderType;
import com.kaii.dentix.domain.userServiceAgreement.dto.request.UserServiceAgreementRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{4,12}$", message = "아이디는 숫자나 영문만 사용 가능해요.")
    private String userLoginIdentifier;

    @NotBlank
    @Pattern(regexp = "^[가-힣a-zA-Z\s]{2,100}$", message = "별명은 한글이나 영문으로 작성해 주세요.")
    private String userName;

    @NotNull
    private GenderType userGender;

    @NotBlank
    @Size(min = 8, max = 20, message = "비밀번호는 최소 8자부터 최대 20자입니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^&*]).{8,20}$", message = "비밀번호는 영문과 특수문자가 필수입니다.")
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
