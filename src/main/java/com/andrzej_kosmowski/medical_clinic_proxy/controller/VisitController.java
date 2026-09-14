package com.andrzej_kosmowski.medical_clinic_proxy.controller;

import com.andrzej_kosmowski.medical_clinic_proxy.dto.VisitDto;
import com.andrzej_kosmowski.medical_clinic_proxy.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/visits")
@RequiredArgsConstructor
public class VisitController {
    private final VisitService visitService;

    @GetMapping("/patient/{patientId}")
    public List<VisitDto> getPatientVisits(@PathVariable Long patientId) {
        return visitService.getPatientVisits(patientId);
    }

    @PatchMapping("/{visitId}/patient/{patientId}")
    public VisitDto assignPatient(@PathVariable Long visitId, @PathVariable Long patientId) {
        return visitService.assignPatient(visitId, patientId);
    }

    @GetMapping("/doctor/{doctorId}/available")
    public List<VisitDto> getAvailableDoctorVisits(@PathVariable Long doctorId) {
        return visitService.getAvailableDoctorVisits(doctorId);
    }

    @GetMapping("/available/search")
    public List<VisitDto> getAvailableSearchVisits(@RequestParam String specialization, @RequestParam LocalDate date) {
        return visitService.getAvailableVisitsBySpecializationAndDate(specialization, date);
    }
}
