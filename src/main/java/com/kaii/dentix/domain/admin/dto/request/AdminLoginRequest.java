package com.kaii.dentix.domain.admin.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kaii.dentix.global.config.PasswordSerializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class AdminLoginRequest {

    @NotBlank(message = "아이디는 필수입니다.")
    @Size(min = 4, max = 12, message = "아이디는 최소 4자부터 최대 12자입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "아이디는 숫자나 영문만 사용 가능해요.")
    private String adminLoginIdentifier;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 최소 8자부터 최대 20자입니다.")
    @JsonSerialize(using = PasswordSerializer.class)
    private String adminPassword;

}
