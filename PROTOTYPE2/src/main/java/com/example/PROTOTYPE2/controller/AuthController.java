package com.example.PROTOTYPE2.controller;

import com.example.PROTOTYPE2.dto.request.LoginRequest;
import com.example.PROTOTYPE2.dto.request.SignupRequest;
import com.example.PROTOTYPE2.dto.response.AuthResponse;
import com.example.PROTOTYPE2.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {


    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        String message = authService.signup(request);
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
