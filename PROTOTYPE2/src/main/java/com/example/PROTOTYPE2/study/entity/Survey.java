package com.example.PROTOTYPE2.study.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "surveys")
public class Survey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "survey_name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_type", nullable = false)
    private ScheduleType scheduleType;

    // Hour of day (0–23) at which recurring tokens are sent. Only used for DAILY/WEEKLY/MONTHLY.
    // Null = no specific hour (treated as 9 by the scheduler).
    @Column(name = "send_hour")
    private Integer sendHour;

    @ManyToOne(optional = false)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    protected Survey() {}

    public Survey(String name, ScheduleType scheduleType, Study study) {
        this.name = name;
        this.scheduleType = scheduleType;
        this.study = study;
    }

    public Long getId()                   { return id; }
    public String getName()               { return name; }
    public ScheduleType getScheduleType() { return scheduleType; }
    public Integer getSendHour()          { return sendHour; }
    public Study getStudy()               { return study; }
    public List<Question> getQuestions()  { return questions; }

    public void setName(String name)                          { this.name = name; }
    public void setScheduleType(ScheduleType scheduleType)    { this.scheduleType = scheduleType; }
    public void setSendHour(Integer sendHour)                 { this.sendHour = sendHour; }
}
