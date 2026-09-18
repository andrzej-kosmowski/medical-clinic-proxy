package com.andrzej_kosmowski.medical_clinic_proxy.exception.handler;

import com.andrzej_kosmowski.medical_clinic_proxy.dto.ErrorMessageDto;
import com.andrzej_kosmowski.medical_clinic_proxy.exception.MedicalClinicProxyException;
import com.andrzej_kosmowski.medical_clinic_proxy.exception.MedicalClinicServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class MedicalClinicProxyExceptionHandler {
    @ExceptionHandler(MedicalClinicProxyException.class)
    public ResponseEntity<ErrorMessageDto> handleMedicalClinicProxyException(MedicalClinicProxyException e) {
        log.error("Business exception: status={}, message={}", e.getStatus(), e.getMessage());
        HttpStatus status = e.getStatus();
        ErrorMessageDto error = new ErrorMessageDto(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                e.getMessage()
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MedicalClinicServiceUnavailableException.class)
    public ResponseEntity<ErrorMessageDto> handleMedicalClinicUnavailable(
            MedicalClinicServiceUnavailableException e) {
        log.error("Medical Clinic service unavailable", e);
        HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
        ErrorMessageDto error = new ErrorMessageDto(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                e.getMessage()
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageDto> handleUnexpectedException(
            Exception exception
    ) {
        log.error("Unexpected exception occurred: {}", exception.getMessage(), exception);
        ErrorMessageDto error = new ErrorMessageDto(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                exception.getMessage()
        );
        return ResponseEntity.internalServerError().body(error);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorMessageDto> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException exception
    ) {
        log.error("MissingServletRequestParameterException occurred: {}", exception.getMessage(), exception);
        ErrorMessageDto error = new ErrorMessageDto(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Required request parameter '%s' is missing".formatted(exception.getParameterName())
        );
        return ResponseEntity.badRequest().body(error);
    }
}
