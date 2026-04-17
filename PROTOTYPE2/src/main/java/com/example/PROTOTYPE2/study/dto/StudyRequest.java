package com.example.PROTOTYPE2.study.dto;

import jakarta.validation.constraints.NotBlank;

public class StudyRequest {

    @NotBlank(message = "Study name is required")
    private String name;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
