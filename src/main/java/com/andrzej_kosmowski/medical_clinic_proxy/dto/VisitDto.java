package com.andrzej_kosmowski.medical_clinic_proxy.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record VisitDto(
        @Schema(description = "Unique ID of the visit", example = "1")
        Long id,
        @Schema(description = "Visit start time", example = "2030-01-01T10:30:00")
        LocalDateTime startTime,
        @Schema(description = "Visit end time", example = "2030-01-01T11:00:00")
        LocalDateTime endTime,
        @Schema(description = "ID of the doctor", example = "2")
        Long doctorId,
        @Schema(description = "ID of the patient. Null when the visit is available", example = "5", nullable = true)
        Long patientId
) {
}
