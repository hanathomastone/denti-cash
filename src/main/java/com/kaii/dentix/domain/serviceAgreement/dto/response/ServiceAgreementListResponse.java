package com.kaii.dentix.domain.serviceAgreement.dto.response;

import com.kaii.dentix.domain.serviceAgreement.dto.ServiceAgreementDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ServiceAgreementListResponse {

    private List<ServiceAgreementDto> agreementList;

    @Override
    public String toString() {
        return "{"
                + super.toString().replace("}", "")
                + ", \"agreementList\":" + agreementList
                + "}";
    }

}
