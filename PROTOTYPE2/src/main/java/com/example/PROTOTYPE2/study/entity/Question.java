package com.example.PROTOTYPE2.study.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String text;

    // e.g. "TEXT", "MULTIPLE_CHOICE", "SCALE"
    @Column(name = "question_type", nullable = false)
    private String type;

    @ManyToOne(optional = false)
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey;
    protected Question() {}

    public Question(String text, String type, Survey survey) {
        this.text = text;
        this.type = type;
        this.survey = survey;
    }

    public Long getId()      { return id; }
    public String getText()  { return text; }
    public String getType()  { return type; }
    public Survey getSurvey(){ return survey; }

    public void setText(String text) { this.text = text; }
    public void setType(String type) { this.type = type; }
}
