package com.kaii.dentix.domain.admin.application;

import com.kaii.dentix.domain.admin.dto.AdminRegisterPatientDto;
import com.kaii.dentix.domain.admin.dto.request.AdminRegisterPatientRequest;
import com.kaii.dentix.domain.patient.dao.PatientRepository;
import com.kaii.dentix.domain.patient.domain.Patient;
import com.kaii.dentix.global.common.error.exception.AlreadyDataException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminPatientService {

    private final PatientRepository patientRepository;

    /**
     *  관리자 환자 등록
     */
    @Transactional
    public AdminRegisterPatientDto adminRegisterPatient(AdminRegisterPatientRequest request){
        boolean isExistPatient = patientRepository.findByPatientPhoneNumberAndPatientName(request.getPatientPhoneNumber(), request.getPatientName()).isPresent();
        if (isExistPatient) throw new AlreadyDataException("이미 등록된 환자입니다.");

        Patient patient = Patient.builder()
                .patientName(request.getPatientName())
                .patientPhoneNumber(request.getPatientPhoneNumber())
                .build();

        patientRepository.save(patient);

        return AdminRegisterPatientDto.builder()
                .patientId(patient.getPatientId())
                .build();
    }


}
