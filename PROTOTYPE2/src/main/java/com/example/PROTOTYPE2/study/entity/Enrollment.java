package com.example.PROTOTYPE2.study.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments")
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    @ManyToOne(optional = false)
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;

    @Column(name = "enrolled_at", nullable = false)
    private LocalDateTime enrolledAt;

    @Column(nullable = false)
    private String status; // ACTIVE, WITHDRAWN

    protected Enrollment() {}

    public Enrollment(Study study, Participant participant) {
        this.study = study;
        this.participant = participant;
        this.enrolledAt = LocalDateTime.now();
        this.status = "ACTIVE";
    }

    public Long getId()                  { return id; }
    public Study getStudy()              { return study; }
    public Participant getParticipant()  { return participant; }
    public LocalDateTime getEnrolledAt() { return enrolledAt; }
    public String getStatus()            { return status; }

    public void setStatus(String status) { this.status = status; }
}
