package com.andrzej_kosmowski.medical_clinic_proxy.service;

import com.andrzej_kosmowski.medical_clinic_proxy.client.MedicalClinicClient;
import com.andrzej_kosmowski.medical_clinic_proxy.dto.VisitDto;
import com.andrzej_kosmowski.medical_clinic_proxy.dto.VisitSearchCriteria;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
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
    void searchVisits_WithDateAndSpecialization_VisitsReturned() {
        // given
        LocalDate date = LocalDate.of(2030, 1, 1);
        VisitSearchCriteria criteria = new VisitSearchCriteria("pediatra", date, null, null, true);
        VisitDto visit = new VisitDto(
                1L,
                LocalDateTime.of(2030, 1, 1, 10, 0),
                LocalDateTime.of(2030, 1, 1, 10, 30),
                2L,
                null);
        when(medicalClinicClient.searchVisits("pediatra", date, null, null, true))
                .thenReturn(List.of(visit));
        // when
        List<VisitDto> result = visitService.searchVisits(criteria);
        // then
        Assertions.assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(LocalDateTime.of(2030, 1, 1, 10, 0), result.get(0).startTime()),
                () -> assertEquals(LocalDateTime.of(2030, 1, 1, 10, 30), result.get(0).endTime()),
                () -> assertEquals(2L, result.get(0).doctorId()),
                () -> assertNull(result.get(0).patientId())
        );
        verify(medicalClinicClient).searchVisits("pediatra", date, null, null, true);
    }

    @Test
    void searchVisits_WithTimeRangeAndSpecialization_VisitsReturned() {
        // given
        LocalDateTime from = LocalDateTime.of(2030, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2030, 1, 31, 23, 59);
        VisitSearchCriteria criteria = new VisitSearchCriteria("cardiologist", null, from, to, false);
        VisitDto visit = new VisitDto(
                1L,
                LocalDateTime.of(2030, 1, 10, 10, 0),
                LocalDateTime.of(2030, 1, 10, 10, 30),
                2L,
                5L
        );
        when(medicalClinicClient.searchVisits("cardiologist", null, from, to, false))
                .thenReturn(List.of(visit));
        // when
        List<VisitDto> result = visitService.searchVisits(criteria);
        // then
        Assertions.assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(1L, result.get(0).id()),
                () -> assertEquals(2L, result.get(0).doctorId()),
                () -> assertEquals(5L, result.get(0).patientId()),
                () -> assertEquals(LocalDateTime.of(2030, 1, 10, 10, 0), result.get(0).startTime())
        );
        verify(medicalClinicClient).searchVisits("cardiologist", null, from, to, false);
    }

    @Test
    void searchVisits_AvailableOnlyWithoutSpecialization_VisitsReturned() {
        // given
        LocalDateTime from = LocalDateTime.of(2030, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2030, 1, 31, 23, 59);
        VisitSearchCriteria criteria = new VisitSearchCriteria(null, null, from, to, true);
        VisitDto visit = new VisitDto(
                1L,
                LocalDateTime.of(2030, 1, 10, 10, 0),
                LocalDateTime.of(2030, 1, 10, 10, 30),
                2L,
                null
        );
        when(medicalClinicClient.searchVisits(null, null, from, to, true))
                .thenReturn(List.of(visit));
        // when
        List<VisitDto> result = visitService.searchVisits(criteria);
        // then
        Assertions.assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(1L, result.get(0).id()),
                () -> assertEquals(2L, result.get(0).doctorId()),
                () -> assertEquals(LocalDateTime.of(2030, 1, 10, 10, 0), result.get(0).startTime()),
                () -> assertNull(result.get(0).patientId())
        );
        verify(medicalClinicClient).searchVisits(null, null, from, to, true);
    }

    @Test
    void searchVisits_NoFilters_VisitsReturned() {
        // given
        VisitSearchCriteria criteria = new VisitSearchCriteria(null, null, null, null, false);
        VisitDto visit = new VisitDto(
                1L,
                LocalDateTime.of(2030, 1, 10, 10, 0),
                LocalDateTime.of(2030, 1, 10, 10, 30),
                2L,
                5L
        );
        when(medicalClinicClient.searchVisits(null, null, null, null, false))
                .thenReturn(List.of(visit));
        // when
        List<VisitDto> result = visitService.searchVisits(criteria);
        // then
        assertEquals(1, result.size());
        verify(medicalClinicClient).searchVisits(null, null, null, null, false);
    }

    @Test
    void deleteVisit_VisitExists_VisitsDeleted() {
        // when
        visitService.deleteVisit(1L);
        // then
        verify(medicalClinicClient).deleteVisit(1L);
    }
}