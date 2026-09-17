package com.andrzej_kosmowski.medical_clinic_proxy.controller;

import com.andrzej_kosmowski.medical_clinic_proxy.dto.ErrorMessageDto;
import com.andrzej_kosmowski.medical_clinic_proxy.dto.VisitDto;
import com.andrzej_kosmowski.medical_clinic_proxy.service.VisitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Visits", description = "Endpoints for managing patient visits")
@RestController
@RequestMapping(value = "/visits", produces = "application/json")
@RequiredArgsConstructor
public class VisitController {
    private final VisitService visitService;

    @Operation(summary = "Get patient visits", description = "Returns all visits assigned to the given patient")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Visits successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Patient not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Medical Clinic service is unavailable",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping("/patient/{patientId}")
    public List<VisitDto> getPatientVisits(@PathVariable Long patientId) {
        return visitService.getPatientVisits(patientId);
    }

    @Operation(summary = "Book a visit", description = "Assigns a patient to an available visit")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Visit successfully booked"),
            @ApiResponse(responseCode = "404", description = "Visit or patient not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "409", description = "Visit is already booked",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Medical Clinic service is unavailable",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PatchMapping("/{visitId}/patient/{patientId}")
    public VisitDto assignPatient(@PathVariable Long visitId, @PathVariable Long patientId) {
        return visitService.assignPatient(visitId, patientId);
    }

    @Operation(summary = "Get available visits for a doctor",
            description = "Returns future visits that are not yet booked")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Available visits successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Doctor not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Medical Clinic service is unavailable",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping("/doctor/{doctorId}/available")
    public List<VisitDto> getAvailableDoctorVisits(@PathVariable Long doctorId) {
        return visitService.getAvailableDoctorVisits(doctorId);
    }

    @Operation(summary = "Search available visits",
            description = "Returns available visits for a given specialization and date")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Available visits successfully retrieved"),
            @ApiResponse(responseCode = "503", description = "Medical Clinic service is unavailable",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping("/available/search")
    public List<VisitDto> getAvailableSearchVisits(@RequestParam String specialization, @RequestParam LocalDate date) {
        return visitService.getAvailableVisitsBySpecializationAndDate(specialization, date);
    }
}
