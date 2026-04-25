package com.example.PROTOTYPE2.study.service;

import com.example.PROTOTYPE2.participant.scheduler.TokenScheduler;
import com.example.PROTOTYPE2.study.dto.*;
import com.example.PROTOTYPE2.study.entity.Enrollment;
import com.example.PROTOTYPE2.study.entity.Question;
import com.example.PROTOTYPE2.study.entity.ScheduleType;
import com.example.PROTOTYPE2.study.entity.Study;
import com.example.PROTOTYPE2.study.entity.Survey;
import com.example.PROTOTYPE2.study.repository.EnrollmentRepository;
import com.example.PROTOTYPE2.study.repository.QuestionRepository;
import com.example.PROTOTYPE2.study.repository.StudyRepository;
import com.example.PROTOTYPE2.study.repository.SurveyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudyService {

    private final StudyRepository      studyRepository;
    private final SurveyRepository     surveyRepository;
    private final QuestionRepository   questionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final TokenScheduler       tokenScheduler;

    public StudyService(StudyRepository studyRepository,
                        SurveyRepository surveyRepository,
                        QuestionRepository questionRepository,
                        EnrollmentRepository enrollmentRepository,
                        TokenScheduler tokenScheduler) {
        this.studyRepository      = studyRepository;
        this.surveyRepository     = surveyRepository;
        this.questionRepository   = questionRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.tokenScheduler       = tokenScheduler;
    }

    @Transactional
    public StudyResponse create(StudyRequest request, Long researcherId) {
        Study study = new Study(request.getName(), researcherId);
        return StudyResponse.from(studyRepository.save(study));
    }

    // Creates a full study with surveys and questions in one transaction
    @Transactional
    public StudyResponse createFull(StudyFullRequest request, Long researcherId) {
        Study study = new Study(request.getName(), researcherId);
        studyRepository.save(study);

        for (SurveyRequest surveyRequest : request.getSurveys()) {
            Survey survey = new Survey(surveyRequest.getName(), surveyRequest.getScheduleType(), study);
            survey.setSendHour(surveyRequest.getSendHour());
            surveyRepository.save(survey);

            for (QuestionRequest questionRequest : surveyRequest.getQuestions()) {
                Question question = new Question(questionRequest.getText(), questionRequest.getType(), survey);
                questionRepository.save(question);
            }
        }

        // Reload from DB so the response includes all nested surveys + questions
        return StudyResponse.from(studyRepository.findById(study.getId()).orElseThrow());
    }

    /**
     * Deploys a study — flips it from DRAFT to ACTIVE and immediately sends the
     * first round of survey tokens to every currently ACTIVE enrolled participant.
     * After this, the TokenScheduler handles recurring sends.
     */
    @Transactional
    public StudyResponse deploy(Long studyId) {
        Study study = findOrThrow(studyId);

        if ("ACTIVE".equals(study.getStatus())) {
            throw new IllegalArgumentException("Study is already deployed");
        }

        study.setStatus("ACTIVE");
        studyRepository.save(study);

        // On deploy, only INSTANT and ONE_TIME surveys fire immediately.
        // DAILY/WEEKLY/MONTHLY are picked up by the scheduler at their configured sendHour.
        List<Enrollment> enrollments = enrollmentRepository.findByStudyId(studyId);
        for (Enrollment enrollment : enrollments) {
            if (!"ACTIVE".equals(enrollment.getStatus())) continue;
            for (Survey survey : study.getSurveys()) {
                if (survey.getScheduleType() == ScheduleType.INSTANT ||
                    survey.getScheduleType() == ScheduleType.ONE_TIME) {
                    tokenScheduler.createAndSendToken(enrollment, survey);
                }
            }
        }

        return StudyResponse.from(study);
    }

    /** ACTIVE → PAUSED. Scheduler stops sending tokens until resumed. */
    @Transactional
    public StudyResponse pause(Long studyId) {
        Study study = findOrThrow(studyId);
        if (!"ACTIVE".equals(study.getStatus())) {
            throw new IllegalArgumentException("Only an active study can be paused");
        }
        study.setStatus("PAUSED");
        return StudyResponse.from(studyRepository.save(study));
    }

    /** PAUSED → ACTIVE. Resumes token sending. */
    @Transactional
    public StudyResponse resume(Long studyId) {
        Study study = findOrThrow(studyId);
        if (!"PAUSED".equals(study.getStatus())) {
            throw new IllegalArgumentException("Only a paused study can be resumed");
        }
        study.setStatus("ACTIVE");
        return StudyResponse.from(studyRepository.save(study));
    }

    /** ACTIVE or PAUSED → DRAFT. Reverts deployment; no new tokens will be sent. */
    @Transactional
    public StudyResponse revert(Long studyId) {
        Study study = findOrThrow(studyId);
        if ("DRAFT".equals(study.getStatus()) || "CLOSED".equals(study.getStatus())) {
            throw new IllegalArgumentException("Study cannot be reverted from its current status");
        }
        study.setStatus("DRAFT");
        return StudyResponse.from(studyRepository.save(study));
    }

    /** Closes the study permanently. No more tokens will be sent. */
    @Transactional
    public StudyResponse close(Long studyId) {
        Study study = findOrThrow(studyId);
        if ("CLOSED".equals(study.getStatus())) {
            throw new IllegalArgumentException("Study is already closed");
        }
        study.setStatus("CLOSED");
        return StudyResponse.from(studyRepository.save(study));
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
