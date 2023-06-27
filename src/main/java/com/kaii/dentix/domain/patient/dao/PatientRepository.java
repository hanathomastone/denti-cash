package com.kaii.dentix.domain.patient.dao;

import com.kaii.dentix.domain.patient.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByPatientPhoneNumberAndPatientName(String patientPhoneNumber, String patientName);

}