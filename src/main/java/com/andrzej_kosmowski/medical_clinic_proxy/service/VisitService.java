package com.andrzej_kosmowski.medical_clinic_proxy.service;

import com.andrzej_kosmowski.medical_clinic_proxy.client.MedicalClinicClient;
import com.andrzej_kosmowski.medical_clinic_proxy.dto.VisitDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VisitService {
    private final MedicalClinicClient medicalClinicClient;

    public List<VisitDto> getPatientVisits(Long patientId) {
        return medicalClinicClient.getPatientVisits(patientId);
    }

    public VisitDto assignPatient(Long visitId, Long patientId) {
        log.info("Assigning patientId={} to visitId={}", patientId, visitId);
        return medicalClinicClient.assignPatient(visitId, patientId);
    }

    public List<VisitDto> getAvailableDoctorVisits(Long doctorId) {
        return medicalClinicClient.getAvailableDoctorVisits(doctorId);
    }

    public List<VisitDto> getAvailableVisitsBySpecializationAndDate(String specialization, LocalDate date) {
        return medicalClinicClient.getAvailableSearchVisits(specialization, date);
    }

    public List<VisitDto> getVisitsBySpecializationAndTimeRange(
            String specialization, LocalDateTime from, LocalDateTime to) {
        return medicalClinicClient.getVisitsBySpecializationAndTimeRange(specialization, from, to);
    }

    public List<VisitDto> getAvailableVisits(String specialization, LocalDateTime from, LocalDateTime to) {
        return medicalClinicClient.getAvailableVisits(specialization, from, to);
    }

    public List<VisitDto> getDoctorVisits(Long doctorId) {
        return medicalClinicClient.getDoctorVisits(doctorId);
    }

    public void deleteVisit(Long visitId) {
        log.info("Deleting visitId={}", visitId);
        medicalClinicClient.deleteVisit(visitId);
        log.info("Visit deleted successfully: visitId={}", visitId);
    }
}
