package com.andrzej_kosmowski.medical_clinic_proxy.fallback;

import com.andrzej_kosmowski.medical_clinic_proxy.client.MedicalClinicClient;
import com.andrzej_kosmowski.medical_clinic_proxy.dto.VisitDto;
import com.andrzej_kosmowski.medical_clinic_proxy.exception.MedicalClinicProxyException;
import com.andrzej_kosmowski.medical_clinic_proxy.exception.MedicalClinicServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
public class MedicalClinicFallbackFactory implements FallbackFactory<MedicalClinicClient> {
    @Override
    public MedicalClinicClient create(Throwable cause) {
        log.error("An exception occurred when calling the MedicalClinicClient", cause);
        if (cause instanceof MedicalClinicProxyException exception) {
            throw exception;
        }
        return new MedicalClinicClient() {
            @Override
            public List<VisitDto> getPatientVisits(Long patientId) {
                throw new MedicalClinicServiceUnavailableException();
            }
            @Override
            public VisitDto assignPatient(Long visitId, Long patientId) {
                throw new MedicalClinicServiceUnavailableException();
            }
            @Override
            public List<VisitDto> getAvailableDoctorVisits(Long doctorId) {
                throw new MedicalClinicServiceUnavailableException();
            }
            @Override
            public List<VisitDto> getAvailableSearchVisits(String specialization, LocalDate date) {
                throw new MedicalClinicServiceUnavailableException();
            }
        };
    }
}
