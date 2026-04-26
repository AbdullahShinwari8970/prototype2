package com.example.PROTOTYPE2.participant.repository;

import com.example.PROTOTYPE2.participant.entity.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResponseRepository extends JpaRepository<Response, Long> {

    List<Response> findBySurveyTokenId(Long surveyTokenId);

    @Query("SELECT r FROM Response r JOIN r.surveyToken tok JOIN tok.survey sv WHERE sv.study.id = :studyId ORDER BY tok.promptedAt ASC, tok.enrollment.participant.id ASC, r.question.id ASC")
    List<Response> findByStudyId(@Param("studyId") Long studyId);
}
