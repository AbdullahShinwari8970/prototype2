package com.example.PROTOTYPE2.auth.controller;

import com.example.PROTOTYPE2.auth.dto.request.LoginRequest;
import com.example.PROTOTYPE2.auth.dto.request.SignupRequest;
import com.example.PROTOTYPE2.auth.dto.response.AuthResponse;
import com.example.PROTOTYPE2.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

//Combines Controller + ResponseBody annotation meaning every method returns a -
// -JSON instead of a HTML View.
@RestController
@RequestMapping("/api/auth") //all endpoints are prefixed with this.
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

//Valid ->triggers the validation annotations defined in the DTO fields before method is executed.
//RequestBody -> deserializes the incoming JSON body into DTO object.
