package com.example.PROTOTYPE2.study.controller;

import com.example.PROTOTYPE2.study.dto.QuestionRequest;
import com.example.PROTOTYPE2.study.dto.QuestionResponse;
import com.example.PROTOTYPE2.study.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/surveys/{surveyId}/questions")
@Tag(name = "Questions", description = "Add and retrieve questions for a survey")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @Operation(summary = "Add a question to a survey")
    @PostMapping
    public ResponseEntity<?> createQuestion(@PathVariable Long surveyId,
                                            @Valid @RequestBody QuestionRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(questionService.create(surveyId, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Get all questions for a survey")
    @GetMapping
    public ResponseEntity<?> getQuestionsBySurvey(@PathVariable Long surveyId) {
        try {
            return ResponseEntity.ok(questionService.getBySurvey(surveyId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
