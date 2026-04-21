package com.example.PROTOTYPE2.participant.repository;

import com.example.PROTOTYPE2.participant.entity.Response;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResponseRepository extends JpaRepository<Response, Long> {

    List<Response> findBySurveyTokenId(Long surveyTokenId);
}
