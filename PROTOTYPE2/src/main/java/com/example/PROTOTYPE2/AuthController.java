package com.example.PROTOTYPE2;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    //Not sure if these are meant to be controller classes, what are service classes then???


    
    @PostMapping("/signup")
    public void signUp(@RequestBody int c) { //Edit this methods input parameters.
    }

    @PostMapping("/login")
    public void login(@RequestBody int c) { //Edit input parameters.
    }
}
