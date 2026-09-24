package com.andrzej_kosmowski.medical_clinic_proxy.controller;

import com.andrzej_kosmowski.medical_clinic_proxy.dto.VisitDto;
import com.andrzej_kosmowski.medical_clinic_proxy.dto.VisitSearchCriteria;
import com.andrzej_kosmowski.medical_clinic_proxy.service.VisitService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class VisitControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    VisitService visitService;

    @Test
    void getPatientVisits_Response200() throws Exception {
        // given
        VisitDto visit = new VisitDto(
                1L,
                LocalDateTime.of(2030, 1, 1, 10, 0),
                LocalDateTime.of(2030, 1, 1, 10, 30),
                2L,
                5L
        );
        when(visitService.getPatientVisits(5L)).thenReturn(List.of(visit));
        // when & then
        mockMvc.perform(get("/visits/patient/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].startTime").value("2030-01-01T10:00:00"))
                .andExpect(jsonPath("$[0].endTime").value("2030-01-01T10:30:00"))
                .andExpect(jsonPath("$[0].doctorId").value(2))
                .andExpect(jsonPath("$[0].patientId").value(5));
    }

    @Test
    void getDoctorVisits_Response200() throws Exception {
        // given
        VisitDto visit = new VisitDto(
                1L,
                LocalDateTime.of(2030, 1, 1, 10, 0),
                LocalDateTime.of(2030, 1, 1, 10, 30),
                2L,
                5L
        );
        when(visitService.getDoctorVisits(2L)).thenReturn(List.of(visit));
        // when & then
        mockMvc.perform(get("/visits/doctor/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].startTime").value("2030-01-01T10:00:00"))
                .andExpect(jsonPath("$[0].endTime").value("2030-01-01T10:30:00"))
                .andExpect(jsonPath("$[0].doctorId").value(2))
                .andExpect(jsonPath("$[0].patientId").value(5));
    }

    @Test
    void assignPatient_Response200() throws Exception {
        // given
        VisitDto visit = new VisitDto(
                1L,
                LocalDateTime.of(2030, 1, 1, 10, 0),
                LocalDateTime.of(2030, 1, 1, 10, 30),
                2L,
                5L
        );
        when(visitService.assignPatient(1L, 5L)).thenReturn(visit);
        // when & then
        mockMvc.perform(patch("/visits/1/patient/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.doctorId").value(2))
                .andExpect(jsonPath("$.patientId").value(5));
    }

    @Test
    void getAvailableDoctorVisits_Response200() throws Exception {
        // given
        VisitDto visit = new VisitDto(
                1L,
                LocalDateTime.of(2030, 1, 1, 10, 0),
                LocalDateTime.of(2030, 1, 1, 10, 30),
                2L,
                null
        );
        when(visitService.getAvailableDoctorVisits(2L)).thenReturn(List.of(visit));
        // when & then
        mockMvc.perform(get("/visits/doctor/2/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].doctorId").value(2))
                .andExpect(jsonPath("$[0].patientId").doesNotExist());
    }

    @Test
    void searchVisits_WithDateAndSpecialization_Response200() throws Exception {
        // given
        VisitDto visit = new VisitDto(
                1L,
                LocalDateTime.of(2030, 1, 1, 10, 0),
                LocalDateTime.of(2030, 1, 1, 10, 30),
                2L,
                null
        );
        VisitSearchCriteria criteria = new VisitSearchCriteria(
                "pediatra", LocalDate.of(2030, 1, 1), null, null, true);
        when(visitService.searchVisits(criteria)).thenReturn(List.of(visit));
        // when & then
        mockMvc.perform(get("/visits/search")
                        .param("specialization", "pediatra")
                        .param("date", "2030-01-01")
                        .param("availableOnly", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].startTime").value("2030-01-01T10:00:00"))
                .andExpect(jsonPath("$[0].doctorId").value(2));
    }

    @Test
    void searchVisits_WithTimeRangeAndSpecialization_Response200() throws Exception {
        // given
        LocalDateTime from = LocalDateTime.of(2030, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2030, 1, 31, 23, 59);
        VisitDto visit = new VisitDto(
                1L,
                LocalDateTime.of(2030, 1, 10, 10, 0),
                LocalDateTime.of(2030, 1, 10, 10, 30),
                2L,
                5L);
        VisitSearchCriteria criteria = new VisitSearchCriteria("Cardiologist", null, from, to, false);
        when(visitService.searchVisits(criteria)).thenReturn(List.of(visit));
        // when & then
        mockMvc.perform(get("/visits/search")
                        .param("specialization", "Cardiologist")
                        .param("from", "2030-01-01T00:00:00")
                        .param("to", "2030-01-31T23:59:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].doctorId").value(2))
                .andExpect(jsonPath("$[0].patientId").value(5))
                .andExpect(jsonPath("$[0].startTime").value("2030-01-10T10:00:00"));
        verify(visitService).searchVisits(criteria);
    }

    @Test
    void searchVisits_AvailableOnlyWithoutSpecialization_Response200() throws Exception {
        // given
        LocalDateTime from = LocalDateTime.of(2030, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2030, 1, 31, 23, 59);
        VisitDto visit = new VisitDto(
                1L,
                LocalDateTime.of(2030, 1, 10, 10, 0),
                LocalDateTime.of(2030, 1, 10, 10, 30),
                2L,
                null);
        VisitSearchCriteria criteria = new VisitSearchCriteria(null, null, from, to, true);
        when(visitService.searchVisits(criteria)).thenReturn(List.of(visit));
        // when & then
        mockMvc.perform(get("/visits/search")
                        .param("from", "2030-01-01T00:00:00")
                        .param("to", "2030-01-31T23:59:00")
                        .param("availableOnly", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].doctorId").value(2))
                .andExpect(jsonPath("$[0].patientId").doesNotExist())
                .andExpect(jsonPath("$[0].startTime").value("2030-01-10T10:00:00"));
        verify(visitService).searchVisits(criteria);
    }

    @Test
    void searchVisits_WithSpecializationAndTimeRangeAvailableOnly_Response200() throws Exception {
        // given
        LocalDateTime from = LocalDateTime.of(2030, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2030, 1, 31, 23, 59);
        VisitDto visit = new VisitDto(
                1L,
                LocalDateTime.of(2030, 1, 10, 10, 0),
                LocalDateTime.of(2030, 1, 10, 10, 30),
                2L,
                null);
        VisitSearchCriteria criteria = new VisitSearchCriteria("Cardiologist", null, from, to, true);
        when(visitService.searchVisits(criteria)).thenReturn(List.of(visit));
        // when & then
        mockMvc.perform(get("/visits/search")
                        .param("specialization", "Cardiologist")
                        .param("from", "2030-01-01T00:00:00")
                        .param("to", "2030-01-31T23:59:00")
                        .param("availableOnly", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].doctorId").value(2))
                .andExpect(jsonPath("$[0].patientId").doesNotExist())
                .andExpect(jsonPath("$[0].startTime").value("2030-01-10T10:00:00"));
        verify(visitService).searchVisits(criteria);
    }

    @Test
    void searchVisits_NoParams_Response200() throws Exception {
        // given
        VisitDto visit = new VisitDto(
                1L,
                LocalDateTime.of(2030, 1, 10, 10, 0),
                LocalDateTime.of(2030, 1, 10, 10, 30),
                2L,
                5L);
        VisitSearchCriteria criteria = new VisitSearchCriteria(null, null, null, null, false);
        when(visitService.searchVisits(criteria)).thenReturn(List.of(visit));
        // when & then
        mockMvc.perform(get("/visits/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
        verify(visitService).searchVisits(criteria);
    }

    @Test
    void deleteVisit_Response200() throws Exception {
        // then & when
        mockMvc.perform(delete("/visits/1"))
                .andExpect(status().isNoContent());

        verify(visitService).deleteVisit(1L);
    }
}