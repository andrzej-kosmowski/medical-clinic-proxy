package com.andrzej_kosmowski.medical_clinic_proxy.client;

import com.andrzej_kosmowski.medical_clinic_proxy.dto.VisitDto;
import com.andrzej_kosmowski.medical_clinic_proxy.exception.MedicalClinicProxyException;
import com.andrzej_kosmowski.medical_clinic_proxy.exception.MedicalClinicServiceUnavailableException;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@EnableWireMock
public class MedicalClinicClientTest {
    @Autowired
    MedicalClinicClient medicalClinicClient;

    @InjectWireMock
    WireMockServer wireMockServer;

    @Test
    void getPatientVisits_Response200_ReturnsVisits() {
        // given
        wireMockServer.stubFor(get("/visits/patient/1")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("patient-visits.json")));
        // when
        List<VisitDto> result = medicalClinicClient.getPatientVisits(1L);
        // then
        Assertions.assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(1L, result.get(0).id()),
                () -> assertEquals(1L, result.get(0).patientId())
        );
    }

    @Test
    void assignPatient_Response200_ReturnsVisit() {
        // given
        wireMockServer.stubFor(patch("/visits/1/patient/2")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("assigned-visit.json")));
        // when
        VisitDto result = medicalClinicClient.assignPatient(1L, 2L);
        // then
        Assertions.assertAll(
                () -> assertEquals(1L, result.id()),
                () -> assertEquals(2L, result.patientId())
        );
    }

    @Test
    void getAvailableDoctorVisits_Response200_ReturnsAvailableDoctorVisits() {
        // given
        wireMockServer.stubFor(get("/visits/doctor/5/available")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("available-doctor-visits.json")));
        // when
        List<VisitDto> result = medicalClinicClient.getAvailableDoctorVisits(5L);
        // then
        Assertions.assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(5L, result.get(0).doctorId()),
                () -> assertNull(result.get(0).patientId())
        );
    }

    @Test
    void searchVisits_WithDateAndSpecialization_ReturnsVisits() {
        // given
        wireMockServer.stubFor(get(urlPathEqualTo("/visits/search"))
                .withQueryParam("specialization", equalTo("pediatra"))
                .withQueryParam("date", equalTo("1.01.2030"))
                .withQueryParam("availableOnly", equalTo("true"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("available-search-visits.json")));
        // when
        List<VisitDto> result = medicalClinicClient.searchVisits("pediatra",
                LocalDate.of(2030, 1, 1), null, null, true);
        // then
        Assertions.assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(3L, result.get(0).doctorId()),
                () -> assertNull(result.get(0).patientId())
        );
    }

    @Test
    void searchVisits_WithTimeRange_ReturnsMatchingVisits() {
        // given
        LocalDateTime from = LocalDateTime.of(2030, 1, 1, 10, 30);
        LocalDateTime to = LocalDateTime.of(2030, 1, 2, 10, 30);

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("d.MM.yyyy, HH:mm");

        wireMockServer.stubFor(get(urlPathEqualTo("/visits/search"))
                .withQueryParam("specialization", equalTo("kardiolog"))
                .withQueryParam("from", equalTo(from.format(formatter)))
                .withQueryParam("to", equalTo(to.format(formatter)))
                .withQueryParam("availableOnly", equalTo("false"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("available-search-visits.json")));

        // when
        List<VisitDto> result = medicalClinicClient.searchVisits(
                "kardiolog",
                null,
                from,
                to,
                false
        );

        // then
        Assertions.assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(3L, result.get(0).doctorId())
        );
    }

    @Test
    void searchVisits_NoFilters_ReturnsAllVisits() {
        // given
        wireMockServer.stubFor(get(urlPathEqualTo("/visits/search"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("available-search-visits.json")));
        // when
        List<VisitDto> result = medicalClinicClient.searchVisits(null, null, null, null, false);
        // then
        assertEquals(1, result.size());
    }

    @Test
    void getPatientVisits_PatientNotFound_ThrowsException() {
        // given
        wireMockServer.stubFor(get("/visits/patient/12")
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("patient-not-found.json")));
        // when
        MedicalClinicProxyException exception = assertThrows(MedicalClinicProxyException.class,
                () -> medicalClinicClient.getPatientVisits(12L));
        // then
        Assertions.assertAll(
                () -> assertEquals("Patient with id 12 not found", exception.getMessage()),
                () -> assertEquals(404, exception.getStatus().value())
        );
    }

    @Test
    void getPatientVisits_ServiceReturns503Twice_RequestIsRetried() {
        // given
        wireMockServer.stubFor(get("/visits/patient/1")
                .inScenario("retry")
                .whenScenarioStateIs(STARTED)
                .willReturn(aResponse().withStatus(503))
                .willSetStateTo("second"));
        wireMockServer.stubFor(get("/visits/patient/1")
                .inScenario("retry")
                .whenScenarioStateIs("second")
                .willReturn(aResponse().withStatus(503))
                .willSetStateTo("third"));
        wireMockServer.stubFor(get("/visits/patient/1")
                .inScenario("retry")
                .whenScenarioStateIs("third")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("patient-visits.json")));
        // when
        List<VisitDto> result = medicalClinicClient.getPatientVisits(1L);
        // then
        Assertions.assertAll(
                () -> assertEquals(1L, result.get(0).id()),
                () -> assertEquals(1L, result.get(0).patientId())
        );
        wireMockServer.verify(3, getRequestedFor(urlEqualTo("/visits/patient/1")));
    }

    @Test
    void getPatientVisits_ServiceReturns503_UsesFallbackFactory() {
        // given
        wireMockServer.stubFor(get("/visits/patient/1")
                .willReturn(aResponse().withStatus(503)));
        // when & then
        assertThrows(MedicalClinicServiceUnavailableException.class,
                () -> medicalClinicClient.getPatientVisits(1L));
        wireMockServer.verify(4, getRequestedFor(urlEqualTo("/visits/patient/1")));
    }
}
