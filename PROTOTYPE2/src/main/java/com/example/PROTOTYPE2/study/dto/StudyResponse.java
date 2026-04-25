package com.example.PROTOTYPE2.study.dto;

import com.example.PROTOTYPE2.study.entity.Study;

import java.util.List;

public class StudyResponse {

    private Long id;
    private String name;
    private Long researcherId;
    private String status;
    private List<SurveySummary> surveys;

    private StudyResponse() {}

    public static StudyResponse from(Study study) {
        StudyResponse res = new StudyResponse();
        res.id           = study.getId();
        res.name         = study.getName();
        res.researcherId = study.getResearcherId();
        res.status       = study.getStatus() != null ? study.getStatus() : "DRAFT";
        res.surveys      = study.getSurveys().stream()
                                .map(s -> new SurveySummary(s.getId(), s.getName(), s.getScheduleType().name()))
                                .toList();
        return res;
    }

    public Long getId()                     { return id; }
    public String getName()                 { return name; }
    public Long getResearcherId()           { return researcherId; }
    public String getStatus()               { return status; }
    public List<SurveySummary> getSurveys() { return surveys; }

    public record SurveySummary(Long id, String name, String scheduleType) {}
}
