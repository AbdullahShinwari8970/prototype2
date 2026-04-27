package com.example.PROTOTYPE2.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data //Lombok generates getters and setters including other methods, automatically
public class SignupRequest {
    @NotBlank
    private String name;

    @NotBlank
    @Email //validation constraint, that email has @ -> has a domain.
    private String email;

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}


//Constraints give error codes before they reach the controller here.