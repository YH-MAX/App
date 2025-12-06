package com.smartwater.backend.controller;

import com.smartwater.backend.dto.CreateReportRequest;
import com.smartwater.backend.dto.PollutionReportResponse;
import com.smartwater.backend.dto.UpdateReportStatusRequest;
import com.smartwater.backend.model.PollutionReport;
import com.smartwater.backend.service.PollutionReportService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class PollutionReportController {

    private final PollutionReportService reportService;

    public PollutionReportController(PollutionReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<PollutionReportResponse> createReport(
            @AuthenticationPrincipal UserDetails currentUser,
            @Valid @RequestBody CreateReportRequest request
    ) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }

        String email = currentUser.getUsername();

        PollutionReport report = reportService.createReport(
                email,
                request.getDescription(),
                request.getPhotoUrl(),
                request.getLocation()
        );

        return ResponseEntity.ok(reportService.toResponse(report));
    }

    @GetMapping("/me")
    public ResponseEntity<List<PollutionReportResponse>> getMyReports(
            @AuthenticationPrincipal UserDetails currentUser) {

        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }

        String email = currentUser.getUsername();

        List<PollutionReportResponse> responses = reportService.getReportsForUser(email)
                .stream()
                .map(reportService::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping
    public ResponseEntity<List<PollutionReportResponse>> getAllReports() {

        List<PollutionReportResponse> responses = reportService.getAllReports()
                .stream()
                .map(reportService::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PollutionReportResponse> updateReportStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReportStatusRequest request
    ) {

        PollutionReport updated = reportService.updateReportStatus(
                id,
                request.getStatus(),
                request.getAdminComment()
        );

        return ResponseEntity.ok(reportService.toResponse(updated));
    }
}
