package com.andrzej_kosmowski.medical_clinic_proxy.controller;

import com.andrzej_kosmowski.medical_clinic_proxy.dto.VisitDto;
import com.andrzej_kosmowski.medical_clinic_proxy.service.VisitService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
        VisitDto visit = new VisitDto(
                1L,
                LocalDateTime.of(2030, 1, 1, 10, 0),
                LocalDateTime.of(2030, 1, 1, 10, 30),
                2L,
                5L
        );

        when(visitService.getPatientVisits(5L))
                .thenReturn(List.of(visit));

        mockMvc.perform(get("/visits/patient/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].startTime").value("2030-01-01T10:00:00"))
                .andExpect(jsonPath("$[0].endTime").value("2030-01-01T10:30:00"))
                .andExpect(jsonPath("$[0].doctorId").value(2))
                .andExpect(jsonPath("$[0].patientId").value(5));
    }

    @Test
    void assignPatient_Response200() throws Exception {
        VisitDto visit = new VisitDto(
                1L,
                LocalDateTime.of(2030, 1, 1, 10, 0),
                LocalDateTime.of(2030, 1, 1, 10, 30),
                2L,
                5L
        );

        when(visitService.assignPatient(1L, 5L))
                .thenReturn(visit);

        mockMvc.perform(patch("/visits/1/patient/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.doctorId").value(2))
                .andExpect(jsonPath("$.patientId").value(5));
    }

    @Test
    void getAvailableDoctorVisits_Response200() throws Exception {
        VisitDto visit = new VisitDto(
                1L,
                LocalDateTime.of(2030, 1, 1, 10, 0),
                LocalDateTime.of(2030, 1, 1, 10, 30),
                2L,
                null
        );

        when(visitService.getAvailableDoctorVisits(2L))
                .thenReturn(List.of(visit));

        mockMvc.perform(get("/visits/doctor/2/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].doctorId").value(2))
                .andExpect(jsonPath("$[0].patientId").doesNotExist());
    }

    @Test
    void getAvailableSearchVisits_Response200() throws Exception {
        VisitDto visit = new VisitDto(
                1L,
                LocalDateTime.of(2030, 1, 1, 10, 0),
                LocalDateTime.of(2030, 1, 1, 10, 30),
                2L,
                null
        );

        when(visitService.getAvailableVisitsBySpecializationAndDate(
                "pediatra",
                LocalDate.of(2030, 1, 1)
        )).thenReturn(List.of(visit));

        mockMvc.perform(get("/visits/available/search")
                        .param("specialization", "pediatra")
                        .param("date", "2030-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].startTime").value("2030-01-01T10:00:00"))
                .andExpect(jsonPath("$[0].doctorId").value(2));
    }
}