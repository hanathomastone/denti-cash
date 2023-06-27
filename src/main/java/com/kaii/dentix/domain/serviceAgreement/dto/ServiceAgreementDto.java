package com.kaii.dentix.domain.serviceAgreement.dto;

import com.kaii.dentix.domain.type.YnType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
@AllArgsConstructor
public class ServiceAgreementDto {

    private Long serviceAgreeId;
    private String serviceAgreeName;
    private String serviceAgreeMenuName;
    private String serviceAgreeFooterName;
    private YnType isServiceAgreeRequired;
    private String serviceAgreePath;

    @Override
    public String toString() {
        return "{"
                + "\"serviceAgreeId\":" + serviceAgreeId
                + ", \"serviceAgreeName\":\"" + serviceAgreeName + "\""
                + ", \"serviceAgreeMenuName\":\"" + serviceAgreeMenuName + "\""
                + ", \"serviceAgreeFooterName\":\"" + serviceAgreeFooterName + "\""
                + ", \"isServiceAgreeRequired\":\"" + isServiceAgreeRequired + "\""
                + ", \"serviceAgreePath\":\"" + serviceAgreePath + "\""
                + "}";
    }

}
