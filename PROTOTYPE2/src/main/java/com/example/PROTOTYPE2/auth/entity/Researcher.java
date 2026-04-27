package com.example.PROTOTYPE2.auth.entity;

import jakarta.persistence.*;
import lombok.*;

//These are all Class level annotations;
@Entity //Marks this class as a JPA entity.
@Table(name = "researchers") //Specifies the table name.
@Getter @Setter @NoArgsConstructor //Lombok generates all these automatically,
@AllArgsConstructor //Lombok generates an empty constructor.
@Builder
public class Researcher {

    @Id //Marks the primery key field.
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Configures Automatic Key Generation.
    @Column(name = "researcher_id") //sets the column name
    private Integer researcherId;

    @Column(nullable = false, unique = true) //database constriant -> email cannot be null and has to be unique.
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String name;
}
