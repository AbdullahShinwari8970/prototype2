package com.example.PROTOTYPE2.participant.entity;

import com.example.PROTOTYPE2.study.entity.Enrollment;
import com.example.PROTOTYPE2.study.entity.Survey;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "survey_tokens")
public class SurveyToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(optional = false)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @ManyToOne(optional = false)
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey;

    @Column(name = "prompted_at", nullable = false)
    private LocalDateTime promptedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(nullable = false)
    private String status; // PENDING, COMPLETED, EXPIRED

    @OneToMany(mappedBy = "surveyToken", cascade = CascadeType.ALL)
    private List<Response> responses = new ArrayList<>();

    protected SurveyToken() {}

    public SurveyToken(Enrollment enrollment, Survey survey, LocalDateTime expiresAt) {
        this.token       = UUID.randomUUID().toString();
        this.enrollment  = enrollment;
        this.survey      = survey;
        this.promptedAt  = LocalDateTime.now();
        this.expiresAt   = expiresAt;
        this.status      = "PENDING";
    }

    public Long getId()                    { return id; }
    public String getToken()               { return token; }
    public Enrollment getEnrollment()      { return enrollment; }
    public Survey getSurvey()              { return survey; }
    public LocalDateTime getPromptedAt()   { return promptedAt; }
    public LocalDateTime getExpiresAt()    { return expiresAt; }
    public LocalDateTime getCompletedAt()  { return completedAt; }
    public String getStatus()              { return status; }
    public List<Response> getResponses()   { return responses; }

    public void markCompleted() {
        this.status      = "COMPLETED";
        this.completedAt = LocalDateTime.now();
    }

    public void setStatus(String status) { this.status = status; }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
