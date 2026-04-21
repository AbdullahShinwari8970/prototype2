package com.example.PROTOTYPE2.participant.dto;

import com.example.PROTOTYPE2.participant.entity.SurveyToken;
import com.example.PROTOTYPE2.study.entity.Question;

import java.util.List;

/**
 * Returned to the participant when they open their survey link.
 * Contains the survey name and all questions they need to answer.
 */
public class SurveyViewResponse {

    private String surveyName;
    private List<QuestionDto> questions;

    private SurveyViewResponse() {}

    public static SurveyViewResponse from(SurveyToken surveyToken) {
        SurveyViewResponse dto = new SurveyViewResponse();
        dto.surveyName = surveyToken.getSurvey().getName();
        dto.questions  = surveyToken.getSurvey().getQuestions()
                .stream()
                .map(QuestionDto::from)
                .toList();
        return dto;
    }

    public String getSurveyName()          { return surveyName; }
    public List<QuestionDto> getQuestions() { return questions; }

    public record QuestionDto(Long id, String text, String type) {
        public static QuestionDto from(Question q) {
            return new QuestionDto(q.getId(), q.getText(), q.getType());
        }
    }
}
