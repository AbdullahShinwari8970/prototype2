package com.example.PROTOTYPE2.study.repository;

import com.example.PROTOTYPE2.study.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
    List<Survey> findByStudyId(Long studyId);
}
