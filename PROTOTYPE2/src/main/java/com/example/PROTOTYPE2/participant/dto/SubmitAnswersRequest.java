package com.example.PROTOTYPE2.participant.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Sent by the participant when they submit their answers.
 * Contains a list of answers, one per question.
 */
public class SubmitAnswersRequest {

    @NotEmpty(message = "Answers cannot be empty")
    private List<AnswerDto> answers;

    public List<AnswerDto> getAnswers() { return answers; }
    public void setAnswers(List<AnswerDto> answers) { this.answers = answers; }

    public static class AnswerDto {

        private Long questionId;
        private String answerValue;

        public Long getQuestionId()       { return questionId; }
        public String getAnswerValue()    { return answerValue; }

        public void setQuestionId(Long questionId)       { this.questionId = questionId; }
        public void setAnswerValue(String answerValue)   { this.answerValue = answerValue; }
    }
}
