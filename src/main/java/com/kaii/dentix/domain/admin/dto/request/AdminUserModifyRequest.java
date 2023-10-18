package com.kaii.dentix.domain.admin.dto.request;

import com.kaii.dentix.domain.type.GenderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class AdminUserModifyRequest {

    @NotNull(message = "사용자 고유번호를 입력해 주세요.")
    private Long userId; // 사용자 고유번호

    @NotBlank(message = "아이디는 필수입니다.")
    @Size(min = 4, max = 12, message = "아이디는 최소 4자부터 최대 12자입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "아이디는 숫자나 영문만 사용 가능해요.")
    private String userLoginIdentifier; // 사용자 아이디

    @NotBlank(message = "이름은 필수입니다.")
    @Size(min = 2, max = 100, message = "이름은 최소 2자 이상 입력해야 됩니다.")
    @Pattern(regexp = "^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z\\s]+$", message = "이름은 한글이나 영문으로만 입력해 주세요.")
    private String userName; // 사용자 이름

    private GenderType userGender; // 사용자 성별
}
