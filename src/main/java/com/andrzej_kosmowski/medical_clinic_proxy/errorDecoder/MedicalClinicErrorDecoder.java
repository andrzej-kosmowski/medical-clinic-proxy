package com.andrzej_kosmowski.medical_clinic_proxy.errorDecoder;

import com.andrzej_kosmowski.medical_clinic_proxy.dto.ErrorMessageDto;
import com.andrzej_kosmowski.medical_clinic_proxy.exception.MedicalClinicProxyException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class MedicalClinicErrorDecoder implements ErrorDecoder {
    private final ObjectMapper objectMapper;
    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();
        if (status >= 500) {
            log.error("Medical Clinic call failed: method={}, status={}, url={}",
                    methodKey, status, response.request().url());
        } else {
            log.error("Medical Clinic call returned client error: method={}, status={}, url={}",
                    methodKey, status, response.request().url());
        }
        return switch (status) {
            case 503 -> createRetryableException(response);
            default -> createMedicalClinicException(methodKey, response);
        };
    }

    private Exception createRetryableException(Response response) {
        log.error("Medical Clinic returned 503, will retry: method={}, url={}",
                response.request().httpMethod(), response.request().url());
        return new RetryableException(
                response.status(),
                "Medical Clinic service is temporarily unavailable",
                response.request().httpMethod(),
                (Long) null,
                response.request()
        );
    }

    private Exception createMedicalClinicException(String methodKey, Response response) {
        ErrorMessageDto error = readError(response);
        if (error != null) {
            return new MedicalClinicProxyException(error.message(), HttpStatus.valueOf(error.status())) {};
        }
        return defaultDecoder.decode(methodKey, response);
    }

    private ErrorMessageDto readError(Response response) {
        if (response.body() == null) {
            log.error("Medical Clinic response body is empty, status={}", response.status());
            return null;
        }
        try {
            return objectMapper.readValue(response.body().asInputStream(), ErrorMessageDto.class);
        } catch (IOException exception) {
            log.error("Failed to read Medical Clinic response body, status={}", response.status(), exception);
            return null;
        }
    }
}
