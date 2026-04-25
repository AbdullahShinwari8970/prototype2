package com.example.PROTOTYPE2.study.dto;

import com.example.PROTOTYPE2.study.entity.Survey;

import java.util.List;

public class SurveyResponse {

    private Long id;
    private String name;
    private String scheduleType;
    private Integer sendHour;
    private Long studyId;
    private String studyName;
    private List<QuestionResponse> questions;

    private SurveyResponse() {}

    public static SurveyResponse from(Survey survey) {
        SurveyResponse res = new SurveyResponse();
        res.id           = survey.getId();
        res.name         = survey.getName();
        res.scheduleType = survey.getScheduleType().name();
        res.sendHour     = survey.getSendHour();
        res.studyId      = survey.getStudy().getId();
        res.studyName    = survey.getStudy().getName();
        res.questions    = survey.getQuestions().stream()
                                 .map(QuestionResponse::from)
                                 .toList();
        return res;
    }

    public Long getId()                        { return id; }
    public String getName()                    { return name; }
    public String getScheduleType()            { return scheduleType; }
    public Integer getSendHour()               { return sendHour; }
    public Long getStudyId()                   { return studyId; }
    public String getStudyName()               { return studyName; }
    public List<QuestionResponse> getQuestions(){ return questions; }
}
