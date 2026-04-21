package com.example.PROTOTYPE2.participant.service;

import com.example.PROTOTYPE2.participant.dto.SubmitAnswersRequest;
import com.example.PROTOTYPE2.participant.dto.SurveyViewResponse;
import com.example.PROTOTYPE2.participant.entity.Response;
import com.example.PROTOTYPE2.participant.entity.SurveyToken;
import com.example.PROTOTYPE2.participant.repository.ResponseRepository;
import com.example.PROTOTYPE2.participant.repository.SurveyTokenRepository;
import com.example.PROTOTYPE2.study.entity.Enrollment;
import com.example.PROTOTYPE2.study.entity.Question;
import com.example.PROTOTYPE2.study.entity.Survey;
import com.example.PROTOTYPE2.study.repository.EnrollmentRepository;
import com.example.PROTOTYPE2.study.repository.QuestionRepository;
import com.example.PROTOTYPE2.study.repository.SurveyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ParticipantService {

    private final SurveyTokenRepository surveyTokenRepository;
    private final ResponseRepository    responseRepository;
    private final EnrollmentRepository  enrollmentRepository;
    private final SurveyRepository      surveyRepository;
    private final QuestionRepository    questionRepository;

    public ParticipantService(SurveyTokenRepository surveyTokenRepository,
                              ResponseRepository responseRepository,
                              EnrollmentRepository enrollmentRepository,
                              SurveyRepository surveyRepository,
                              QuestionRepository questionRepository) {
        this.surveyTokenRepository = surveyTokenRepository;
        this.responseRepository    = responseRepository;
        this.enrollmentRepository  = enrollmentRepository;
        this.surveyRepository      = surveyRepository;
        this.questionRepository    = questionRepository;
    }

    /**
     * GET /api/participant/survey/{token}
     * Validates the token and returns the survey questions for the participant to answer.
     */
    @Transactional(readOnly = true)
    public SurveyViewResponse getSurvey(String token) {
        SurveyToken surveyToken = findValidToken(token);
        return SurveyViewResponse.from(surveyToken);
    }

    /**
     * POST /api/participant/survey/{token}/submit
     * Saves all answers and marks the token as COMPLETED.
     */
    @Transactional
    public void submitAnswers(String token, SubmitAnswersRequest request) {
        SurveyToken surveyToken = findValidToken(token);

        // Build a map of questionId -> Question for fast lookup
        List<Question> questions = questionRepository.findBySurveyId(surveyToken.getSurvey().getId());
        Map<Long, Question> questionMap = questions.stream()
                .collect(Collectors.toMap(Question::getId, Function.identity()));

        // Save one Response row per answer
        for (SubmitAnswersRequest.AnswerDto answer : request.getAnswers()) {
            Question question = questionMap.get(answer.getQuestionId());
            if (question == null) {
                throw new IllegalArgumentException("Question not found: " + answer.getQuestionId());
            }
            responseRepository.save(new Response(surveyToken, question, answer.getAnswerValue()));
        }

        // Mark the token as completed — records completion timestamp
        surveyToken.markCompleted();
        surveyTokenRepository.save(surveyToken);
    }

    /**
     * POST /api/participant/generate-token (temporary — for testing only)
     * Manually generates a survey token for a given enrollment and survey.
     * In production this will be replaced by the scheduler.
     */
    @Transactional
    public SurveyToken generateToken(Long enrollmentId, Long surveyId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found: " + enrollmentId));

        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("Survey not found: " + surveyId));

        // Prevent duplicate pending tokens for same enrollment + survey
        if (surveyTokenRepository.existsByEnrollmentIdAndSurveyIdAndStatus(enrollmentId, surveyId, "PENDING")) {
            throw new IllegalArgumentException("A pending token already exists for this enrollment and survey");
        }

        // Expiry based on schedule type
        LocalDateTime expiresAt = switch (survey.getScheduleType()) {
            case DAILY   -> LocalDateTime.now().plusDays(1);
            case WEEKLY  -> LocalDateTime.now().plusDays(7);
            case MONTHLY -> LocalDateTime.now().plusDays(30);
            case ONE_TIME -> LocalDateTime.now().plusDays(7);
        };

        return surveyTokenRepository.save(new SurveyToken(enrollment, survey, expiresAt));
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private SurveyToken findValidToken(String token) {
        SurveyToken surveyToken = surveyTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid survey link"));

        if (surveyToken.isExpired()) {
            surveyToken.setStatus("EXPIRED");
            surveyTokenRepository.save(surveyToken);
            throw new IllegalArgumentException("This survey link has expired");
        }

        if ("COMPLETED".equals(surveyToken.getStatus())) {
            throw new IllegalArgumentException("This survey has already been submitted");
        }

        return surveyToken;
    }
}
