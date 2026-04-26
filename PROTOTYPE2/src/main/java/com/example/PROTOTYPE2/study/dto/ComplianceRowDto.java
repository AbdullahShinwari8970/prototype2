package com.example.PROTOTYPE2.study.dto;

public class ComplianceRowDto {

    private Long participantId;
    private String participantName;
    private Long surveyId;
    private String surveyName;
    private long sent;
    private long completed;
    private int completionRate;

    public ComplianceRowDto(Long participantId, String participantName,
                            Long surveyId, String surveyName,
                            long sent, long completed) {
        this.participantId   = participantId;
        this.participantName = participantName;
        this.surveyId        = surveyId;
        this.surveyName      = surveyName;
        this.sent            = sent;
        this.completed       = completed;
        this.completionRate  = sent > 0 ? (int) Math.round((completed * 100.0) / sent) : 0;
    }

    public Long getParticipantId()      { return participantId; }
    public String getParticipantName()  { return participantName; }
    public Long getSurveyId()           { return surveyId; }
    public String getSurveyName()       { return surveyName; }
    public long getSent()               { return sent; }
    public long getCompleted()          { return completed; }
    public int getCompletionRate()      { return completionRate; }
}
