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
    @Pattern(regexp = "^[0-9]{10,11}$", message = "휴대폰 번호는 숫자만 입력해 주세요.")
    private String patientPhoneNumber;

    @NotBlank
    @Pattern(regexp = "^[가-힣a-zA-Z\s]{2,100}$", message = "이름은 한글이나 영문으로만 입력해 주세요.")
    private String patientName;

}
