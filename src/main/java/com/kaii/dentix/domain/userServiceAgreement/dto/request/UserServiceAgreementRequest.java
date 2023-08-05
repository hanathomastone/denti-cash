package com.kaii.dentix.domain.userServiceAgreement.dto.request;

import com.kaii.dentix.domain.type.YnType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
@AllArgsConstructor
public class UserServiceAgreementRequest {

    @Min(value = 1)
    private Long userServiceAgreeId;

    @NotNull(message = "동의 여부는 필수입니다.")
    private YnType isUserServiceAgree;

}
