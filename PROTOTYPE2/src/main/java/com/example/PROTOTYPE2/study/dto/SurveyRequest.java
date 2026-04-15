package com.example.PROTOTYPE2.study.dto;

import com.example.PROTOTYPE2.study.entity.ScheduleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SurveyRequest {

    @NotBlank(message = "Survey name is required")
    private String name;

    @NotNull(message = "Schedule type is required (ONE_TIME, DAILY, WEEKLY, MONTHLY)")
    private ScheduleType scheduleType;

    // Used by composite endpoint — optional when adding a survey standalone
    private List<QuestionRequest> questions = new ArrayList<>();

    public String getName()                    { return name; }
    public ScheduleType getScheduleType()      { return scheduleType; }
    public List<QuestionRequest> getQuestions(){ return questions; }

    public void setName(String name)                          { this.name = name; }
    public void setScheduleType(ScheduleType scheduleType)    { this.scheduleType = scheduleType; }
    public void setQuestions(List<QuestionRequest> questions) { this.questions = questions; }
}
