package com.andrzej_kosmowski.medical_clinic_proxy.controller;

import com.andrzej_kosmowski.medical_clinic_proxy.dto.DoctorDto;
import com.andrzej_kosmowski.medical_clinic_proxy.service.DoctorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DoctorControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    DoctorService doctorService;

    @Test
    void getDoctorsBySpecialization_Response200() throws Exception {
        // given
        DoctorDto doctor = new DoctorDto(1L, "doctor@test.pl", "Jan", "Nowak",
                "Cardiologist");
        when(doctorService.getDoctorsBySpecialization("Cardiologist")).thenReturn(List.of(doctor));
        // when & then
        mockMvc.perform(get("/doctors/specialization/Cardiologist"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("doctor@test.pl"))
                .andExpect(jsonPath("$[0].firstName").value("Jan"))
                .andExpect(jsonPath("$[0].lastName").value("Nowak"))
                .andExpect(jsonPath("$[0].specialization").value("Cardiologist"));
        verify(doctorService).getDoctorsBySpecialization("Cardiologist");
    }
}
