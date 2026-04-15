package com.example.PROTOTYPE2.study.controller;

import com.example.PROTOTYPE2.study.dto.SurveyRequest;
import com.example.PROTOTYPE2.study.dto.SurveyResponse;
import com.example.PROTOTYPE2.study.service.SurveyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Surveys", description = "Create and retrieve surveys within a study")
public class SurveyController {

    private final SurveyService surveyService;

    public SurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @Operation(summary = "Add a survey to a study")
    @PostMapping("/api/studies/{studyId}/surveys")
    public ResponseEntity<?> createSurvey(@PathVariable Long studyId,
                                          @Valid @RequestBody SurveyRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(surveyService.create(studyId, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Get all surveys for a study")
    @GetMapping("/api/studies/{studyId}/surveys")
    public ResponseEntity<?> getSurveysByStudy(@PathVariable Long studyId) {
        try {
            return ResponseEntity.ok(surveyService.getByStudy(studyId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Get a survey by ID including its questions")
    @GetMapping("/api/surveys/{id}")
    public ResponseEntity<?> getSurveyById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(surveyService.getById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
