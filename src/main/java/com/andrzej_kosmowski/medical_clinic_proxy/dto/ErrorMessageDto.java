package com.andrzej_kosmowski.medical_clinic_proxy.dto;

import java.time.LocalDateTime;

public record ErrorMessageDto(
        LocalDateTime timestamp,
        Integer status,
        String error,
        String message
) {
}