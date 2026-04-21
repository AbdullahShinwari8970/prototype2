package com.example.PROTOTYPE2.study.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public class StudyFullRequest {

    @NotBlank(message = "Study name is required")
    private String name;

    @NotNull(message = "At least one survey is required")
    private List<SurveyRequest> surveys = new ArrayList<>();

    public String getName()                  { return name; }
    public List<SurveyRequest> getSurveys()  { return surveys; }

    public void setName(String name)                    { this.name = name; }
    public void setSurveys(List<SurveyRequest> surveys) { this.surveys = surveys; }
}
