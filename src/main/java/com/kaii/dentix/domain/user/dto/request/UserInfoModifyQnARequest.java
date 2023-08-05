package com.kaii.dentix.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class UserInfoModifyQnARequest {

    @NotNull(message = "질문 선택은 필수입니다.")
    private Long findPwdQuestionId;

    @NotBlank(message = "답변은 필수입니다.")
    private String findPwdAnswer;

}
