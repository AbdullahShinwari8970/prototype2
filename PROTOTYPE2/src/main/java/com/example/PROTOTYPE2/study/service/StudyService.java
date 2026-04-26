package com.example.PROTOTYPE2.study.service;

import com.example.PROTOTYPE2.participant.entity.SurveyToken;
import com.example.PROTOTYPE2.participant.repository.ResponseRepository;
import com.example.PROTOTYPE2.participant.repository.SurveyTokenRepository;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudyService {

    private final StudyRepository        studyRepository;
    private final SurveyRepository       surveyRepository;
    private final QuestionRepository     questionRepository;
    private final EnrollmentRepository   enrollmentRepository;
    private final TokenScheduler         tokenScheduler;
    private final ResponseRepository     responseRepository;
    private final SurveyTokenRepository  surveyTokenRepository;

    public StudyService(StudyRepository studyRepository,
                        SurveyRepository surveyRepository,
                        QuestionRepository questionRepository,
                        EnrollmentRepository enrollmentRepository,
                        TokenScheduler tokenScheduler,
                        ResponseRepository responseRepository,
                        SurveyTokenRepository surveyTokenRepository) {
        this.studyRepository      = studyRepository;
        this.surveyRepository     = surveyRepository;
        this.questionRepository   = questionRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.tokenScheduler       = tokenScheduler;
        this.responseRepository   = responseRepository;
        this.surveyTokenRepository = surveyTokenRepository;
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

        // On deploy, ONE_TIME surveys fire immediately.
        // DAILY/WEEKLY/MONTHLY are picked up by the scheduler at their configured sendHour.
        List<Enrollment> enrollments = enrollmentRepository.findByStudyId(studyId);
        for (Enrollment enrollment : enrollments) {
            if (!"ACTIVE".equals(enrollment.getStatus())) continue;
            for (Survey survey : study.getSurveys()) {
                if (survey.getScheduleType() == ScheduleType.ONE_TIME) {
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

    @Transactional(readOnly = true)
    public List<ComplianceRowDto> getCompliance(Long studyId, Long researcherId) {
        Study study = findOrThrow(studyId);
        if (!study.getResearcherId().equals(researcherId)) {
            throw new IllegalArgumentException("Access denied: you do not own this study");
        }

        List<SurveyToken> tokens = surveyTokenRepository.findByStudyId(studyId);

        // Group by participantId + surveyId
        Map<String, long[]> counts = new LinkedHashMap<>();
        Map<String, String[]> labels = new LinkedHashMap<>();

        for (SurveyToken tok : tokens) {
            Long participantId = tok.getEnrollment().getParticipant().getId();
            String participantName = tok.getEnrollment().getParticipant().getName();
            Long surveyId = tok.getSurvey().getId();
            String surveyName = tok.getSurvey().getName();

            String key = participantId + "-" + surveyId;
            counts.computeIfAbsent(key, k -> new long[]{0, 0});
            labels.computeIfAbsent(key, k -> new String[]{participantName, surveyName, String.valueOf(participantId), String.valueOf(surveyId)});

            counts.get(key)[0]++;
            if ("COMPLETED".equals(tok.getStatus())) {
                counts.get(key)[1]++;
            }
        }

        List<ComplianceRowDto> rows = new ArrayList<>();
        for (Map.Entry<String, long[]> entry : counts.entrySet()) {
            String[] meta = labels.get(entry.getKey());
            rows.add(new ComplianceRowDto(
                    Long.valueOf(meta[2]),
                    meta[0],
                    Long.valueOf(meta[3]),
                    meta[1],
                    entry.getValue()[0],
                    entry.getValue()[1]
            ));
        }
        return rows;
    }

    @Transactional(readOnly = true)
    public List<ExportRowDto> getExportRows(Long studyId, Long researcherId) {
        Study study = findOrThrow(studyId);
        if (!study.getResearcherId().equals(researcherId)) {
            throw new IllegalArgumentException("Access denied: you do not own this study");
        }
        return responseRepository.findByStudyId(studyId).stream()
                .map(r -> new ExportRowDto(
                        r.getSurveyToken().getSurvey().getStudy().getId(),
                        r.getSurveyToken().getSurvey().getStudy().getName(),
                        r.getSurveyToken().getSurvey().getId(),
                        r.getSurveyToken().getSurvey().getName(),
                        r.getSurveyToken().getSurvey().getScheduleType(),
                        r.getSurveyToken().getEnrollment().getParticipant().getId(),
                        r.getSurveyToken().getEnrollment().getParticipant().getName(),
                        r.getQuestion().getId(),
                        r.getQuestion().getText(),
                        r.getAnswerValue(),
                        r.getSurveyToken().getPromptedAt(),
                        r.getSubmittedAt()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public String exportAsCsv(Long studyId, Long researcherId) {
        List<ExportRowDto> rows = getExportRows(studyId, researcherId);

        StringBuilder sb = new StringBuilder();
        sb.append("study_id,study_name,survey_id,survey_name,schedule_type,participant_id,participant_name,question_id,question_text,answer_value,prompted_at,submitted_at,response_delay_seconds\n");

        for (ExportRowDto row : rows) {
            sb.append(row.getStudyId()).append(",");
            sb.append(escapeCsv(row.getStudyName())).append(",");
            sb.append(row.getSurveyId()).append(",");
            sb.append(escapeCsv(row.getSurveyName())).append(",");
            sb.append(row.getScheduleType()).append(",");
            sb.append(row.getParticipantId()).append(",");
            sb.append(escapeCsv(row.getParticipantName())).append(",");
            sb.append(row.getQuestionId()).append(",");
            sb.append(escapeCsv(row.getQuestionText())).append(",");
            sb.append(escapeCsv(row.getAnswerValue())).append(",");
            sb.append(row.getPromptedAt()).append(",");
            sb.append(row.getSubmittedAt()).append(",");
            sb.append(row.getResponseDelaySeconds()).append("\n");
        }

        return sb.toString();
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    Study findOrThrow(Long id) {
        return studyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Study not found with id: " + id));
    }
}
