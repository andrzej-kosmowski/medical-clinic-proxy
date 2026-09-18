package com.andrzej_kosmowski.medical_clinic_proxy.client;

import com.andrzej_kosmowski.medical_clinic_proxy.config.FeignConfiguration;
import com.andrzej_kosmowski.medical_clinic_proxy.dto.DoctorDto;
import com.andrzej_kosmowski.medical_clinic_proxy.dto.VisitDto;
import com.andrzej_kosmowski.medical_clinic_proxy.fallback.MedicalClinicFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@FeignClient(
        name = "medical-clinic-client",
        url = "${app.medical-clinic.url}",
        configuration = FeignConfiguration.class,
        fallbackFactory = MedicalClinicFallbackFactory.class
)
public interface MedicalClinicClient {
    @GetMapping("/visits/patient/{patientId}")
    List<VisitDto> getPatientVisits(@PathVariable Long patientId);

    @PatchMapping("/visits/{visitId}/patient/{patientId}")
    VisitDto assignPatient(@PathVariable Long visitId, @PathVariable Long patientId);

    @GetMapping("/visits/doctor/{doctorId}/available")
    List<VisitDto> getAvailableDoctorVisits(@PathVariable Long doctorId);

    @GetMapping("/visits/available/search")
    List<VisitDto> getAvailableSearchVisits(@RequestParam String specialization, @RequestParam LocalDate date);

    @GetMapping("/visits/search")
    List<VisitDto> getVisitsBySpecializationAndTimeRange(
            @RequestParam String specialization, @RequestParam LocalDateTime from, @RequestParam LocalDateTime to);

    @GetMapping("/visits/available/range")
    List<VisitDto> getAvailableVisits(
            @RequestParam(required = false) String specialization,
            @RequestParam LocalDateTime from,
            @RequestParam LocalDateTime to);

    @GetMapping("/visits/doctor/{doctorId}")
    List<VisitDto> getDoctorVisits(@PathVariable Long doctorId);

    @DeleteMapping("/visits/{visitId}")
    void deleteVisit(@PathVariable Long visitId);

    @GetMapping("/doctors/specialization/{specialization}")
    List<DoctorDto> getDoctorsBySpecialization(@PathVariable String specialization);
}
