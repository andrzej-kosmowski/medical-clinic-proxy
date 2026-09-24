package com.andrzej_kosmowski.medical_clinic_proxy.service;

import com.andrzej_kosmowski.medical_clinic_proxy.client.MedicalClinicClient;
import com.andrzej_kosmowski.medical_clinic_proxy.dto.DoctorDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DoctorServiceTest {
    DoctorService doctorService;
    MedicalClinicClient medicalClinicClient;

    @BeforeEach
    void setUp() {
        this.medicalClinicClient = Mockito.mock(MedicalClinicClient.class);
        this.doctorService = new DoctorService(medicalClinicClient);
    }

    @Test
    void getDoctorsBySpecialization_DoctorsExists_DoctorsReturned() {
        // given
        DoctorDto doctor = new DoctorDto(1L, "doctor@test.pl", "Jan", "Nowak",
                "Cardiologist");
        when(medicalClinicClient.getDoctorsBySpecialization("Cardiologist")).thenReturn(List.of(doctor));
        // when
        List<DoctorDto> result = doctorService.getDoctorsBySpecialization("Cardiologist");
        // then
        Assertions.assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(1L, result.get(0).id()),
                () -> assertEquals("doctor@test.pl", result.get(0).email()),
                () -> assertEquals("Jan", result.get(0).firstName()),
                () -> assertEquals("Nowak", result.get(0).lastName()),
                () -> assertEquals("Cardiologist", result.get(0).specialization())
        );
        verify(medicalClinicClient).getDoctorsBySpecialization("Cardiologist");
    }

    @Test
    void getDoctorsBySpecialization_DoctorsNotExists_EmptyListReturned() {
        // given
        when(medicalClinicClient.getDoctorsBySpecialization("")).thenReturn(List.of());
        // when
        List<DoctorDto> result = doctorService.getDoctorsBySpecialization("");
        // then
        assertTrue(result.isEmpty());
        verify(medicalClinicClient).getDoctorsBySpecialization("");
    }
}
