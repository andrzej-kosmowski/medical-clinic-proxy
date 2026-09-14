package com.andrzej_kosmowski.medical_clinic_proxy.service;

import com.andrzej_kosmowski.medical_clinic_proxy.client.MedicalClinicClient;
import com.andrzej_kosmowski.medical_clinic_proxy.dto.VisitDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class VisitServiceTest {
    VisitService visitService;
    MedicalClinicClient medicalClinicClient;

    @BeforeEach
    void setUp() {
        this.medicalClinicClient = Mockito.mock(MedicalClinicClient.class);
        this.visitService = new VisitService(medicalClinicClient);
    }

    @Test
    void getPatientVisits_PatientExists_VisitsReturned() {
        // given
        VisitDto visit = new VisitDto(
                1L,
                LocalDateTime.of(2030, 1, 1, 10, 0),
                LocalDateTime.of(2030, 1, 1, 10, 30),
                1L,
                1L);
        when(medicalClinicClient.getPatientVisits(1L))
                .thenReturn(List.of(visit));
        // when
        List<VisitDto> result = visitService.getPatientVisits(1L);
        // then
        Assertions.assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(1L, result.get(0).patientId()),
                () -> assertEquals(1L, result.get(0).doctorId())
        );
    }

    @Test
    void assignPatient_VisitExists_VisitReturned() {
        // given
        VisitDto visit = new VisitDto(
                1L,
                LocalDateTime.of(2030, 1, 1, 10, 0),
                LocalDateTime.of(2030, 1, 1, 10, 30),
                1L,
                1L);
        when(medicalClinicClient.assignPatient(1L, 1L)).thenReturn(visit);
        // when
        VisitDto result = visitService.assignPatient(1L, 1L);
        // then
        Assertions.assertAll(
                () -> assertEquals(1L, result.id()),
                () -> assertEquals(1L, result.patientId()),
                () -> assertEquals(1L, result.doctorId())
        );
    }

    @Test
    void getAvailableDoctorVisits_DoctorExists_VisitsReturned() {
        // given
        VisitDto visit = new VisitDto(
                1L,
                LocalDateTime.of(2030, 1, 1, 10, 0),
                LocalDateTime.of(2030, 1, 1, 10, 30),
                1L,
                null);
        when(medicalClinicClient.getAvailableDoctorVisits(1L)).thenReturn(List.of(visit));
        // when
        List<VisitDto> result = visitService.getAvailableDoctorVisits(1L);
        // then
        Assertions.assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(1L, result.get(0).doctorId()),
                () -> assertNull(result.get(0).patientId())
        );
    }

    @Test
    void getAvailableVisitsBySpecializationAndDate_VisitExists_VisitsReturned() {
        // when
        LocalDate date = LocalDate.of(2030, 1, 1);
        VisitDto visit = new VisitDto(
                1L,
                LocalDateTime.of(2030, 1, 1, 10, 0),
                LocalDateTime.of(2030, 1, 1, 10, 30),
                2L,
                null);
        when(medicalClinicClient.getAvailableSearchVisits("pediatra", date)).thenReturn(List.of(visit));
        // when
        List<VisitDto> result = visitService.getAvailableVisitsBySpecializationAndDate("pediatra", date);
        // then
        Assertions.assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(LocalDateTime.of(2030, 1, 1, 10, 0),
                        result.get(0).startTime()),
                () -> assertEquals(LocalDateTime.of(2030, 1, 1, 10, 30),
                        result.get(0).endTime()),
                () -> assertEquals(2L, result.get(0).doctorId()),
                () -> assertNull(result.get(0).patientId())
        );
    }
}