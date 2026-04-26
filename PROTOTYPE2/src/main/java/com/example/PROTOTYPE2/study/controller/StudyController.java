package com.example.PROTOTYPE2.study.controller;

import com.example.PROTOTYPE2.shared.security.ResearcherDetailsImpl;
import com.example.PROTOTYPE2.study.dto.ComplianceRowDto;
import com.example.PROTOTYPE2.study.dto.EnrollmentRequest;
import com.example.PROTOTYPE2.study.dto.ExportRowDto;
import com.example.PROTOTYPE2.study.dto.StudyFullRequest;
import com.example.PROTOTYPE2.study.dto.StudyRequest;
import com.example.PROTOTYPE2.study.dto.StudyResponse;
import com.example.PROTOTYPE2.study.service.EnrollmentService;
import com.example.PROTOTYPE2.study.service.StudyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Studies", description = "Manage studies and participant enrollment")
public class StudyController {

    private final StudyService studyService;
    private final EnrollmentService enrollmentService;

    public StudyController(StudyService studyService, EnrollmentService enrollmentService) {
        this.studyService = studyService;
        this.enrollmentService = enrollmentService;
    }

    @Operation(summary = "Create a study")
    @PostMapping("/api/studies")
    public ResponseEntity<?> createStudy(@Valid @RequestBody StudyRequest request,
                                         @AuthenticationPrincipal ResearcherDetailsImpl principal) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(studyService.create(request, principal.getId().longValue()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Create a full study with surveys and questions in one request")
    @PostMapping("/api/studies/full")
    public ResponseEntity<?> createStudyFull(@Valid @RequestBody StudyFullRequest request,
                                             @AuthenticationPrincipal ResearcherDetailsImpl principal) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(studyService.createFull(request, principal.getId().longValue()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Get all studies, optionally filtered by researcherId")
    @GetMapping("/api/studies")
    public ResponseEntity<List<StudyResponse>> getAllStudies(
            @RequestParam(required = false) Long researcherId) {
        if (researcherId != null) {
            return ResponseEntity.ok(studyService.getByResearcher(researcherId));
        }
        return ResponseEntity.ok(studyService.getAll());
    }

    @Operation(summary = "Get a study by ID")
    @GetMapping("/api/studies/{id}")
    public ResponseEntity<?> getStudyById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(studyService.getById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Enroll a participant in a study")
    @PostMapping("/api/studies/{studyId}/participants")
    public ResponseEntity<?> enrollParticipant(@PathVariable Long studyId,
                                               @Valid @RequestBody EnrollmentRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(enrollmentService.enroll(studyId, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Get all participants enrolled in a study")
    @GetMapping("/api/studies/{studyId}/participants")
    public ResponseEntity<?> getParticipantsByStudy(@PathVariable Long studyId) {
        try {
            return ResponseEntity.ok(enrollmentService.getByStudy(studyId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Withdraw a participant from a study")
    @DeleteMapping("/api/studies/{studyId}/participants/{enrollmentId}")
    public ResponseEntity<?> withdrawParticipant(@PathVariable Long studyId,
                                                  @PathVariable Long enrollmentId) {
        try {
            return ResponseEntity.ok(enrollmentService.withdraw(studyId, enrollmentId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Deploy a study — flips status to ACTIVE and sends first tokens to all enrolled participants")
    @PostMapping("/api/studies/{studyId}/deploy")
    public ResponseEntity<?> deployStudy(@PathVariable Long studyId) {
        try {
            return ResponseEntity.ok(studyService.deploy(studyId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Pause a study — stops token sending until resumed")
    @PostMapping("/api/studies/{studyId}/pause")
    public ResponseEntity<?> pauseStudy(@PathVariable Long studyId) {
        try {
            return ResponseEntity.ok(studyService.pause(studyId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Resume a paused study")
    @PostMapping("/api/studies/{studyId}/resume")
    public ResponseEntity<?> resumeStudy(@PathVariable Long studyId) {
        try {
            return ResponseEntity.ok(studyService.resume(studyId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Revert a deployed study back to DRAFT")
    @PostMapping("/api/studies/{studyId}/revert")
    public ResponseEntity<?> revertStudy(@PathVariable Long studyId) {
        try {
            return ResponseEntity.ok(studyService.revert(studyId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Close a study permanently")
    @PostMapping("/api/studies/{studyId}/close")
    public ResponseEntity<?> closeStudy(@PathVariable Long studyId) {
        try {
            return ResponseEntity.ok(studyService.close(studyId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Get compliance overview — tokens sent vs completed per participant per survey")
    @GetMapping("/api/studies/{studyId}/compliance")
    public ResponseEntity<?> getCompliance(@PathVariable Long studyId,
                                           @AuthenticationPrincipal ResearcherDetailsImpl principal) {
        try {
            List<ComplianceRowDto> rows = studyService.getCompliance(studyId, principal.getId().longValue());
            return ResponseEntity.ok(rows);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Export study responses as CSV")
    @GetMapping("/api/studies/{studyId}/export/csv")
    public ResponseEntity<?> exportCsv(@PathVariable Long studyId,
                                       @AuthenticationPrincipal ResearcherDetailsImpl principal) {
        try {
            String csv = studyService.exportAsCsv(studyId, principal.getId().longValue());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"study-" + studyId + "-export.csv\"")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(csv);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Export study responses as JSON")
    @GetMapping("/api/studies/{studyId}/export/json")
    public ResponseEntity<?> exportJson(@PathVariable Long studyId,
                                        @AuthenticationPrincipal ResearcherDetailsImpl principal) {
        try {
            List<ExportRowDto> rows = studyService.getExportRows(studyId, principal.getId().longValue());
            return ResponseEntity.ok(rows);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        }
    }
}
