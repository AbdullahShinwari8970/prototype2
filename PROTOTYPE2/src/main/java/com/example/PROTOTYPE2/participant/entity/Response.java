package com.example.PROTOTYPE2.participant.entity;

import com.example.PROTOTYPE2.study.entity.Question;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "responses")
public class Response {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "survey_token_id", nullable = false)
    private SurveyToken surveyToken;

    @ManyToOne(optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "answer_value", nullable = false, columnDefinition = "TEXT")
    private String answerValue;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    protected Response() {}

    public Response(SurveyToken surveyToken, Question question, String answerValue) {
        this.surveyToken  = surveyToken;
        this.question     = question;
        this.answerValue  = answerValue;
        this.submittedAt  = LocalDateTime.now();
    }

    public Long getId()                   { return id; }
    public SurveyToken getSurveyToken()   { return surveyToken; }
    public Question getQuestion()         { return question; }
    public String getAnswerValue()        { return answerValue; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
}
