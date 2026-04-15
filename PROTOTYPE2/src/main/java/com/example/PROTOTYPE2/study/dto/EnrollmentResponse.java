package com.example.PROTOTYPE2.study.dto;

import com.example.PROTOTYPE2.study.entity.Enrollment;

import java.time.LocalDateTime;

public class EnrollmentResponse {

    private Long enrollmentId;
    private Long studyId;
    private String studyName;
    private Long participantId;
    private String participantName;
    private String participantEmail;
    private LocalDateTime enrolledAt;
    private String status;

    private EnrollmentResponse() {}

    public static EnrollmentResponse from(Enrollment e) {
        EnrollmentResponse res = new EnrollmentResponse();
        res.enrollmentId      = e.getId();
        res.studyId           = e.getStudy().getId();
        res.studyName         = e.getStudy().getName();
        res.participantId     = e.getParticipant().getId();
        res.participantName   = e.getParticipant().getName();
        res.participantEmail  = e.getParticipant().getEmail();
        res.enrolledAt        = e.getEnrolledAt();
        res.status            = e.getStatus();
        return res;
    }

    public Long getEnrollmentId()        { return enrollmentId; }
    public Long getStudyId()             { return studyId; }
    public String getStudyName()         { return studyName; }
    public Long getParticipantId()       { return participantId; }
    public String getParticipantName()   { return participantName; }
    public String getParticipantEmail()  { return participantEmail; }
    public LocalDateTime getEnrolledAt() { return enrolledAt; }
    public String getStatus()            { return status; }
}
