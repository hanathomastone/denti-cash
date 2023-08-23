package com.kaii.dentix.domain.userServiceAgreement.dto.request;

import com.kaii.dentix.domain.type.YnType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class UserModifyServiceAgreeRequest {

    @NotNull(message = "동의 항목은 필수입니다.")
    private Long serviceAgreeId;

    @NotNull(message = "동의 여부는 필수입니다.")
    private YnType isUserServiceAgree;

}
