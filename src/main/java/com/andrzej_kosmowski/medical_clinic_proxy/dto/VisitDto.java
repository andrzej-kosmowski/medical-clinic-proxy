package com.andrzej_kosmowski.medical_clinic_proxy.dto;

import java.time.LocalDateTime;

public record VisitDto(
        Long id,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Long doctorId,
        Long patientId
) {
}
