package com.example.PROTOTYPE2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Integer researcherId;
    private String email;
    private String name;

    public AuthResponse(String token, Integer researcherId, String email, String name) {
        this.token = token;
        this.researcherId = researcherId;
        this.email = email;
        this.name = name;
    }
}
