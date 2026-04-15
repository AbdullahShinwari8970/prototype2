package com.example.PROTOTYPE2.study.service;

import com.example.PROTOTYPE2.study.dto.QuestionRequest;
import com.example.PROTOTYPE2.study.dto.QuestionResponse;
import com.example.PROTOTYPE2.study.entity.Question;
import com.example.PROTOTYPE2.study.entity.Survey;
import com.example.PROTOTYPE2.study.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final SurveyService surveyService;

    public QuestionService(QuestionRepository questionRepository, SurveyService surveyService) {
        this.questionRepository = questionRepository;
        this.surveyService = surveyService;
    }

    @Transactional
    public QuestionResponse create(Long surveyId, QuestionRequest request) {
        Survey survey = surveyService.findOrThrow(surveyId);
        Question question = new Question(request.getText(), request.getType(), survey);
        return QuestionResponse.from(questionRepository.save(question));
    }

    @Transactional(readOnly = true)
    public List<QuestionResponse> getBySurvey(Long surveyId) {
        // Verify survey exists before fetching
        surveyService.findOrThrow(surveyId);
        return questionRepository.findBySurveyId(surveyId).stream()
                .map(QuestionResponse::from)
                .toList();
    }
}
