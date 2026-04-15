package com.example.PROTOTYPE2.study.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EnrollmentRequest {

    @NotBlank(message = "Participant name is required")
    private String name;

    @NotBlank(message = "Participant email is required")
    @Email(message = "Must be a valid email")
    private String email;

    public String getName()  { return name; }
    public String getEmail() { return email; }

    public void setName(String name)   { this.name = name; }
    public void setEmail(String email) { this.email = email; }
}
