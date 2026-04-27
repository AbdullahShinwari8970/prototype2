package com.example.PROTOTYPE2.study.dto;

import jakarta.validation.constraints.NotBlank;

public class QuestionRequest {

    @NotBlank(message = "Question text is required")
    private String text;

    @NotBlank(message = "Question type is required")
    private String type;

    public String getText() { return text; }
    public String getType() { return type; }

    public void setText(String text) { this.text = text; }
    public void setType(String type) { this.type = type; }
}
