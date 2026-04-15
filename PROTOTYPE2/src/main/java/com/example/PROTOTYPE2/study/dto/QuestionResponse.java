package com.example.PROTOTYPE2.study.dto;

import com.example.PROTOTYPE2.study.entity.Question;

public class QuestionResponse {

    private Long id;
    private String text;
    private String type;
    private Long surveyId;

    private QuestionResponse() {}

    public static QuestionResponse from(Question q) {
        QuestionResponse res = new QuestionResponse();
        res.id       = q.getId();
        res.text     = q.getText();
        res.type     = q.getType();
        res.surveyId = q.getSurvey().getId();
        return res;
    }

    public Long getId()      { return id; }
    public String getText()  { return text; }
    public String getType()  { return type; }
    public Long getSurveyId(){ return surveyId; }
}
