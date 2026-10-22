package com.andrzej_kosmowski.medical_clinic_proxy.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record DoctorDto(
        @Schema(description = "Unique ID of the doctor", example = "1")
        Long id,
        @Schema(description = "Doctor email", example = "doctor@test.pl")
        String email,
        @Schema(description = "Doctor first name", example = "Jan")
        String firstName,
        @Schema(description = "Doctor last name", example = "Nowak")
        String lastName,
        @Schema(description = "Doctor specialization", example = "Cardiologist")
        String specialization
) {
}
