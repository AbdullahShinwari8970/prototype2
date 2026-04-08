package com.example.PROTOTYPE2.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "researchers")
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
@Builder
public class Researcher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "researcher_id")
    private Integer researcherId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String name;

}