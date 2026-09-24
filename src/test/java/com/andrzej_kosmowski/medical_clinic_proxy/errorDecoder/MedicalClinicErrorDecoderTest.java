package com.andrzej_kosmowski.medical_clinic_proxy.errorDecoder;

import com.andrzej_kosmowski.medical_clinic_proxy.dto.ErrorMessageDto;
import com.andrzej_kosmowski.medical_clinic_proxy.exception.MedicalClinicProxyException;
import feign.Request;
import feign.Response;
import feign.RetryableException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MedicalClinicErrorDecoderTest {
    MedicalClinicErrorDecoder medicalClinicErrorDecoder;
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        medicalClinicErrorDecoder = new MedicalClinicErrorDecoder(objectMapper);
    }

    @Test
    void decode_503_ReturnsRetryableException() {
        // given
        Response response = createResponse(503, null);
        // when
        Exception result = medicalClinicErrorDecoder.decode("MedicalClinicClient#getPatientVisits", response);
        // then
        Assertions.assertAll(
                () -> assertInstanceOf(RetryableException.class, result),
                () -> assertEquals(503, response.status()),
                () -> assertEquals("Medical Clinic service is temporarily unavailable", result.getMessage())
        );
    }

    @Test
    void decode_404WithErrorBody_ReturnsMedicalClinicProxyException() {
        // given
        ErrorMessageDto error = new ErrorMessageDto(
                null,
                404,
                "Not Found",
                "Patient with id 12 not found"
        );
        Response response = createResponse(404, objectMapper.writeValueAsString(error));
        // when
        Exception result = medicalClinicErrorDecoder.decode("MedicalClinicClient#getPatientVisits", response);
        // then
        MedicalClinicProxyException exception = assertInstanceOf(MedicalClinicProxyException.class, result);
        Assertions.assertAll(
                () -> assertEquals(404, exception.getStatus().value()),
                () -> assertEquals("Patient with id 12 not found", exception.getMessage())
        );
    }

    @Test
    void decode_409WithErrorBody_ReturnsMedicalClinicProxyException() {
        // given
        ErrorMessageDto error = new ErrorMessageDto(
                null,
                409,
                "Conflict",
                "Visit is already booked"
        );
        Response response = createResponse(409, objectMapper.writeValueAsString(error));
        // when
        Exception result = medicalClinicErrorDecoder.decode("MedicalClinicClient#assignPatient", response);
        // then
        MedicalClinicProxyException exception = assertInstanceOf(MedicalClinicProxyException.class, result);
        Assertions.assertAll(
                () -> assertEquals(409, exception.getStatus().value()),
                () -> assertEquals("Visit is already booked", exception.getMessage())
        );
    }

    @Test
    void decode_500WhenResponseBodyIsMissing_UsesDefaultDecoder() {
        // given
        Response response = createResponse(500, null);
        // when
        Exception result = medicalClinicErrorDecoder.decode("MedicalClinicClient#getPatientVisits", response);
        // then
        assertInstanceOf(Exception.class, result);
    }

    private Response createResponse(int status, String body) {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "http://localhost:8080/visits/patient/1",
                Map.of(),
                null,
                null,
                null
        );
        Response.Builder builder = Response.builder()
                .status(status)
                .reason("Test")
                .request(request);
        if (body != null) {
            builder.body(body, StandardCharsets.UTF_8);
        }
        return builder.build();
    }
}