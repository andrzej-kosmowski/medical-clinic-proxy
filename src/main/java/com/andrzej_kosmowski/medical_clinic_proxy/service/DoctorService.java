package com.andrzej_kosmowski.medical_clinic_proxy.service;

import com.andrzej_kosmowski.medical_clinic_proxy.client.MedicalClinicClient;
import com.andrzej_kosmowski.medical_clinic_proxy.dto.DoctorDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final MedicalClinicClient medicalClinicClient;

    public List<DoctorDto> getDoctorsBySpecialization(String specialization) {
        return medicalClinicClient.getDoctorsBySpecialization(specialization);
    }
}
