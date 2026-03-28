package com.example.PROTOTYPE2.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {


//    private final AuthService authService;
//
//    public AuthController(AuthService authService) {
//        this.authService = authService;
//    }

    @PostMapping("/signup")
    public void signUp(@RequestBody int c) { //Edit this methods input parameters.
    }

    @PostMapping("/login")
    public void login(@RequestBody int c) { //Edit input parameters.
    }
}
