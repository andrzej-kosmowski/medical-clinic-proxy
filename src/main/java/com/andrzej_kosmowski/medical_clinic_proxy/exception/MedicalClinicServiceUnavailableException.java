package com.andrzej_kosmowski.medical_clinic_proxy.exception;

import org.springframework.http.HttpStatus;

public class MedicalClinicServiceUnavailableException extends MedicalClinicProxyException {
    public MedicalClinicServiceUnavailableException() {
        super("Medical Clinic API unavailable, try again later", HttpStatus.SERVICE_UNAVAILABLE);
    }
}
