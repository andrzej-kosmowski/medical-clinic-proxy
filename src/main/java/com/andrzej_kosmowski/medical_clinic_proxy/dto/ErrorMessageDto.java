package com.andrzej_kosmowski.medical_clinic_proxy.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ErrorMessageDto(
        @Schema(description = "Time when the error occurred", example = "2030-01-01T10:15:30")
        LocalDateTime timestamp,
        @Schema(description = "HTTP status code", example = "404")
        Integer status,
        @Schema(description = "HTTP status description", example = "Not Found")
        String error,
        @Schema(description = "Detailed error message", example = "Patient with id 5 not found")
        String message
) {
}