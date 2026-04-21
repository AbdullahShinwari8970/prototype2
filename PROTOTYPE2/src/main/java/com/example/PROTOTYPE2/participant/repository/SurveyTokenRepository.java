package com.example.PROTOTYPE2.participant.repository;

import com.example.PROTOTYPE2.participant.entity.SurveyToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SurveyTokenRepository extends JpaRepository<SurveyToken, Long> {

    Optional<SurveyToken> findByToken(String token);

    // Used by scheduler to prevent duplicate tokens for the same enrollment+survey
    boolean existsByEnrollmentIdAndSurveyIdAndStatus(Long enrollmentId, Long surveyId, String status);
}
