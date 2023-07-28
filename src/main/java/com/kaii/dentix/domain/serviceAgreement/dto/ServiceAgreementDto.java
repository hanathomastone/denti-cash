package com.kaii.dentix.domain.serviceAgreement.dto;

import com.kaii.dentix.domain.type.YnType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
@AllArgsConstructor
public class ServiceAgreementDto {

    private Long id;
    private String name;
    private String menuName;
    private String footerName;
    private YnType isServiceAgreeRequired;
    private String path;

}
