package com.example.PROTOTYPE2.study.dto;

import com.example.PROTOTYPE2.study.entity.ScheduleType;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ExportRowDto {

    private Long studyId;
    private String studyName;
    private Long surveyId;
    private String surveyName;
    private String scheduleType;
    private Long participantId;
    private String participantName;
    private Long questionId;
    private String questionText;
    private String answerValue;
    private LocalDateTime promptedAt;
    private LocalDateTime submittedAt;
    private Long responseDelaySeconds;

    public ExportRowDto(Long studyId, String studyName, Long surveyId, String surveyName,
                        ScheduleType scheduleType, Long participantId, String participantName,
                        Long questionId, String questionText, String answerValue,
                        LocalDateTime promptedAt, LocalDateTime submittedAt) {
        this.studyId              = studyId;
        this.studyName            = studyName;
        this.surveyId             = surveyId;
        this.surveyName           = surveyName;
        this.scheduleType         = scheduleType != null ? scheduleType.name() : null;
        this.participantId        = participantId;
        this.participantName      = participantName;
        this.questionId           = questionId;
        this.questionText         = questionText;
        this.answerValue          = answerValue;
        this.promptedAt           = promptedAt;
        this.submittedAt          = submittedAt;
        this.responseDelaySeconds = (promptedAt != null && submittedAt != null)
                ? ChronoUnit.SECONDS.between(promptedAt, submittedAt)
                : null;
    }

    public Long getStudyId()                  { return studyId; }
    public String getStudyName()              { return studyName; }
    public Long getSurveyId()                 { return surveyId; }
    public String getSurveyName()             { return surveyName; }
    public String getScheduleType()           { return scheduleType; }
    public Long getParticipantId()            { return participantId; }
    public String getParticipantName()        { return participantName; }
    public Long getQuestionId()               { return questionId; }
    public String getQuestionText()           { return questionText; }
    public String getAnswerValue()            { return answerValue; }
    public LocalDateTime getPromptedAt()      { return promptedAt; }
    public LocalDateTime getSubmittedAt()     { return submittedAt; }
    public Long getResponseDelaySeconds()     { return responseDelaySeconds; }
}
