package com.kaii.dentix.domain.patient.dao;

import com.kaii.dentix.domain.admin.dto.AdminPatientInfoDto;
import com.kaii.dentix.domain.admin.dto.request.AdminPatientListRequest;
import com.kaii.dentix.domain.patient.domain.QPatient;
import com.kaii.dentix.global.common.dto.PagingRequest;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AdminPatientCustomRepositoryImpl implements AdminPatientCustomRepository{

    private final JPAQueryFactory queryFactory;

    private final QPatient patient = QPatient.patient;

    /**
     *  관리자 환자 목록 조회
     */
    @Override
    public Page<AdminPatientInfoDto> findAll(AdminPatientListRequest request) {
        Pageable paging = new PagingRequest(request.getPage(), request.getSize()).of();

        List<AdminPatientInfoDto> result = queryFactory
                .select(Projections.constructor(AdminPatientInfoDto.class,
                        patient.patientName, patient.patientPhoneNumber))
                .from(patient)
                .where(StringUtils.isNotBlank(request.getPatientNameOrPhoneNumber()) ?
                        patient.patientName.contains(request.getPatientNameOrPhoneNumber()).or(patient.patientPhoneNumber.contains(request.getPatientNameOrPhoneNumber()))
                        : null)
                .orderBy(patient.created.desc())
                .offset(paging.getOffset())
                .limit(paging.getPageSize())
                .fetch();

        // fetchCount Deprecated 로 인해 count 쿼리 구현
        Long total = Optional.ofNullable(queryFactory
                .select(patient.countDistinct())
                .from(patient)
                .where(StringUtils.isNotBlank(request.getPatientNameOrPhoneNumber()) ?
                        patient.patientName.contains(request.getPatientNameOrPhoneNumber()).or(patient.patientPhoneNumber.contains(request.getPatientNameOrPhoneNumber()))
                        : null)
                .fetchOne()).orElse(0L);

        return new PageImpl<>(result, paging, total);
    }

}
