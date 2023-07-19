package com.kaii.dentix.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class UserFindPasswordRequest {

    @NotBlank @Size(min = 4, max = 12)
    private String userLoginIdentifier;

    @NotNull
    private Long findPwdQuestionId;

    @NotBlank
    private String findPwdAnswer;

}
