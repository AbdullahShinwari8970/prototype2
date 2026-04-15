package com.example.PROTOTYPE2.study.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "studies")
public class Study {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "study_name", nullable = false)
    private String name;

    @Column(name = "researcher_id", nullable = false)
    private Long researcherId;

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Survey> surveys = new ArrayList<>();

    protected Study() {}

    public Study(String name, Long researcherId) {
        this.name = name;
        this.researcherId = researcherId;
    }

    public Long getId()              { return id; }
    public String getName()          { return name; }
    public Long getResearcherId()    { return researcherId; }
    public List<Survey> getSurveys() { return surveys; }

    public void setName(String name)             { this.name = name; }
    public void setResearcherId(Long researcherId) { this.researcherId = researcherId; }
}
