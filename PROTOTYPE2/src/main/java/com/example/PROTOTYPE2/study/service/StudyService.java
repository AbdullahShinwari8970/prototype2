package com.example.PROTOTYPE2.study.service;

import com.example.PROTOTYPE2.study.dto.*;
import com.example.PROTOTYPE2.study.entity.Question;
import com.example.PROTOTYPE2.study.entity.Study;
import com.example.PROTOTYPE2.study.entity.Survey;
import com.example.PROTOTYPE2.study.repository.QuestionRepository;
import com.example.PROTOTYPE2.study.repository.StudyRepository;
import com.example.PROTOTYPE2.study.repository.SurveyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudyService {

    private final StudyRepository studyRepository;
    private final SurveyRepository surveyRepository;
    private final QuestionRepository questionRepository;

    public StudyService(StudyRepository studyRepository,
                        SurveyRepository surveyRepository,
                        QuestionRepository questionRepository) {
        this.studyRepository = studyRepository;
        this.surveyRepository = surveyRepository;
        this.questionRepository = questionRepository;
    }

    @Transactional
    public StudyResponse create(StudyRequest request) {
        Study study = new Study(request.getName(), request.getResearcherId());
        return StudyResponse.from(studyRepository.save(study));
    }

    // Creates a full study with surveys and questions in one transaction
    @Transactional
    public StudyResponse createFull(StudyFullRequest request) {
        Study study = new Study(request.getName(), request.getResearcherId());
        studyRepository.save(study);

        for (SurveyRequest surveyRequest : request.getSurveys()) {
            Survey survey = new Survey(surveyRequest.getName(), surveyRequest.getScheduleType(), study);
            surveyRepository.save(survey);

            for (QuestionRequest questionRequest : surveyRequest.getQuestions()) {
                Question question = new Question(questionRequest.getText(), questionRequest.getType(), survey);
                questionRepository.save(question);
            }
        }

        // Reload from DB so the response includes all nested surveys + questions
        return StudyResponse.from(studyRepository.findById(study.getId()).orElseThrow());
    }

    @Transactional(readOnly = true)
    public StudyResponse getById(Long id) {
        return StudyResponse.from(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<StudyResponse> getAll() {
        return studyRepository.findAll().stream()
                .map(StudyResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StudyResponse> getByResearcher(Long researcherId) {
        return studyRepository.findByResearcherId(researcherId).stream()
                .map(StudyResponse::from)
                .toList();
    }

    Study findOrThrow(Long id) {
        return studyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Study not found with id: " + id));
    }
}
