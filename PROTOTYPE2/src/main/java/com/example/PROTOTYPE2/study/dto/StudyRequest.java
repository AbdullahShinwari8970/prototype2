package com.example.PROTOTYPE2.study.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class StudyRequest {

    @NotBlank(message = "Study name is required")
    private String name;

    @NotNull(message = "Researcher ID is required")
    private Long researcherId;

    public String getName()        { return name; }
    public Long getResearcherId()  { return researcherId; }

    public void setName(String name)             { this.name = name; }
    public void setResearcherId(Long researcherId) { this.researcherId = researcherId; }
}
