package com.kaii.dentix.domain.userServiceAgreement.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
@AllArgsConstructor
public class UserServiceAgreementRequest {

    @Min(value = 1)
    private List<Integer> userServiceAgreeIds;

}
