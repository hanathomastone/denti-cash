package com.kaii.dentix.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class UserFindPasswordRequest {

    @NotBlank
    private String userLoginId;

    @NotNull
    private Long findPwdQuestionId;

    @NotBlank
    private String findPwdAnswer;

}
