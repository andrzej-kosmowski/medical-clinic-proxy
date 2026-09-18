package com.andrzej_kosmowski.medical_clinic_proxy.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class MedicalClinicProxyException extends RuntimeException {
    private final HttpStatus status;
    protected MedicalClinicProxyException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
