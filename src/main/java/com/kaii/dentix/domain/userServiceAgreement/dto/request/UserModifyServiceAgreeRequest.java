package com.kaii.dentix.domain.userServiceAgreement.dto.request;

import com.kaii.dentix.domain.userServiceAgreement.dto.UserModifyServiceAgreeList;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class UserModifyServiceAgreeRequest {

    @NotNull(message = "동의 여부는 필수입니다.")
    List<UserModifyServiceAgreeList> serviceAgreeLists;

}
