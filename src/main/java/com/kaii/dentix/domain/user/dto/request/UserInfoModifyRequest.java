package com.kaii.dentix.domain.user.dto.request;

import com.kaii.dentix.domain.type.GenderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class UserInfoModifyRequest {

    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 2, max = 6, message = "닉네임은 최소 4자부터 최대 6자입니다.")
    @Pattern(regexp = "^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z\\s]+$", message = "닉네임은 한글이나 영문으로만 입력해 주세요.")
    private String userName;

    @NotNull(message = "성별은 필수입니다.")
    private GenderType userGender;

}
