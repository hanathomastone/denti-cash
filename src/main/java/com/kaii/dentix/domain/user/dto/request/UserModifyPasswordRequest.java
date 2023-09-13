package com.kaii.dentix.domain.user.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kaii.dentix.global.config.PasswordSerializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class UserModifyPasswordRequest {

    private Long userId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 최소 8자부터 최대 20자입니다.")
    @JsonSerialize(using = PasswordSerializer.class)
    private String userPassword;

}
