package com.andrzej_kosmowski.medical_clinic_proxy.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record VisitSearchCriteria(
        String specialization,
        LocalDate date,
        LocalDateTime from,
        LocalDateTime to,
        boolean availableOnly
) {
}
