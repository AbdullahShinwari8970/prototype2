package com.example.PROTOTYPE2.study.service;

import com.example.PROTOTYPE2.study.dto.SurveyRequest;
import com.example.PROTOTYPE2.study.dto.SurveyResponse;
import com.example.PROTOTYPE2.study.entity.Question;
import com.example.PROTOTYPE2.study.entity.Study;
import com.example.PROTOTYPE2.study.entity.Survey;
import com.example.PROTOTYPE2.study.repository.QuestionRepository;
import com.example.PROTOTYPE2.study.repository.SurveyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final QuestionRepository questionRepository;
    private final StudyService studyService;

    public SurveyService(SurveyRepository surveyRepository,
                         QuestionRepository questionRepository,
                         StudyService studyService) {
        this.surveyRepository = surveyRepository;
        this.questionRepository = questionRepository;
        this.studyService = studyService;
    }

    @Transactional
    public SurveyResponse create(Long studyId, SurveyRequest request) {
        Study study = studyService.findOrThrow(studyId);
        Survey survey = new Survey(request.getName(), request.getScheduleType(), study);
        survey.setSendHour(request.getSendHour());
        surveyRepository.save(survey);

        // Save questions if provided (used by the "add survey with questions" endpoint)
        if (request.getQuestions() != null && !request.getQuestions().isEmpty()) {
            request.getQuestions().forEach(q ->
                questionRepository.save(new Question(q.getText(), q.getType(), survey))
            );
        }

        // Reload so questions are included in response
        return SurveyResponse.from(surveyRepository.findById(survey.getId()).orElseThrow());
    }

    @Transactional(readOnly = true)
    public SurveyResponse getById(Long id) {
        return SurveyResponse.from(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<SurveyResponse> getByStudy(Long studyId) {
        return surveyRepository.findByStudyId(studyId).stream()
                .map(SurveyResponse::from)
                .toList();
    }

    // Package-visible helper used by QuestionService
    Survey findOrThrow(Long id) {
        return surveyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Survey not found with id: " + id));
    }
}
