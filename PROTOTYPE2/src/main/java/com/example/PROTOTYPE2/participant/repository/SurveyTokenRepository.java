package com.example.PROTOTYPE2.participant.repository;

import com.example.PROTOTYPE2.participant.entity.SurveyToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SurveyTokenRepository extends JpaRepository<SurveyToken, Long> {

    Optional<SurveyToken> findByToken(String token);

    // Prevent duplicate PENDING tokens for the same enrollment+survey
    boolean existsByEnrollmentIdAndSurveyIdAndStatus(Long enrollmentId, Long surveyId, String status);

    // Check if any token (any status) has ever been created — used for ONE_TIME guard
    boolean existsByEnrollmentIdAndSurveyId(Long enrollmentId, Long surveyId);

    // Most recent token for an enrollment+survey — used to decide if a recurring token is due
    Optional<SurveyToken> findTopByEnrollmentIdAndSurveyIdOrderByPromptedAtDesc(Long enrollmentId, Long surveyId);
}
