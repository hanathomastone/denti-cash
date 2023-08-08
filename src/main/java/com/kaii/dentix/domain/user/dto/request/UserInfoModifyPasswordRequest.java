package com.kaii.dentix.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoModifyPasswordRequest {

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 최소 8자부터 최대 20자입니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^&*])[a-zA-Z!@#$%^&*0-9]+$", message = "비밀번호는 영문과 특수문자가 필수입니다.")
    private String userPassword;

}
