package com.example.PROTOTYPE2.participant.controller;

import com.example.PROTOTYPE2.participant.dto.SubmitAnswersRequest;
import com.example.PROTOTYPE2.participant.dto.SurveyViewResponse;
import com.example.PROTOTYPE2.participant.entity.SurveyToken;
import com.example.PROTOTYPE2.participant.service.ParticipantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Tag(name = "Participant", description = "Public endpoints for participants to view and submit surveys")
public class ParticipantController {

    private final ParticipantService participantService;

    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    /**
     * Participant opens their survey link.
     * Returns the survey name and all questions to display on the frontend.
     */
    @Operation(summary = "Get survey questions via token link")
    @GetMapping("/api/participant/survey/{token}")
    public ResponseEntity<?> getSurvey(@PathVariable String token) {
        try {
            SurveyViewResponse response = participantService.getSurvey(token);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Participant submits their answers.
     * Saves all responses and marks the token as COMPLETED.
     */
    @Operation(summary = "Submit answers for a survey token")
    @PostMapping("/api/participant/survey/{token}/submit")
    public ResponseEntity<?> submitAnswers(@PathVariable String token,
                                           @Valid @RequestBody SubmitAnswersRequest request) {
        try {
            participantService.submitAnswers(token, request);
            return ResponseEntity.ok(Map.of("message", "Survey submitted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Temporary endpoint — manually generate a survey token for testing.
     * In production this will be handled by the scheduler.
     */
    @Operation(summary = "Generate a survey token manually (testing only)")
    @PostMapping("/api/participant/generate-token")
    public ResponseEntity<?> generateToken(@RequestParam Long enrollmentId,
                                           @RequestParam Long surveyId) {
        try {
            SurveyToken token = participantService.generateToken(enrollmentId, surveyId);
            return ResponseEntity.ok(Map.of(
                    "token", token.getToken(),
                    "expiresAt", token.getExpiresAt().toString(),
                    "surveyUrl", "/api/participant/survey/" + token.getToken()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
