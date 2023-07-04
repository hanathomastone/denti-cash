package com.kaii.dentix.domain.user.dto.request;

import com.kaii.dentix.domain.type.GenderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class UserInfoModifyRequest {

    @NotBlank
    private String userName;

    @NotNull
    private GenderType userGender;

    @NotBlank
    private String userBirth;

}
