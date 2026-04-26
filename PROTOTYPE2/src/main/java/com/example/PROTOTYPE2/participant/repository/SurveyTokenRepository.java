package com.example.PROTOTYPE2.participant.repository;

import com.example.PROTOTYPE2.participant.entity.SurveyToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SurveyTokenRepository extends JpaRepository<SurveyToken, Long> {

    Optional<SurveyToken> findByToken(String token);

    boolean existsByEnrollmentIdAndSurveyIdAndStatus(Long enrollmentId, Long surveyId, String status);

    boolean existsByEnrollmentIdAndSurveyId(Long enrollmentId, Long surveyId);

    Optional<SurveyToken> findTopByEnrollmentIdAndSurveyIdOrderByPromptedAtDesc(Long enrollmentId, Long surveyId);

    @Query("""
        SELECT tok FROM SurveyToken tok
        JOIN tok.enrollment e
        JOIN tok.survey sv
        WHERE sv.study.id = :studyId
    """)
    List<SurveyToken> findByStudyId(@Param("studyId") Long studyId);
}
