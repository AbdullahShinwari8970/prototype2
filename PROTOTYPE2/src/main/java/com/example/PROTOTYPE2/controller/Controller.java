package com.example.PROTOTYPE2.controller;


import com.example.PROTOTYPE2.dto.FormDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
//@CrossOrigin(origins = "http://localhost:5173")
public class Controller {

    @GetMapping("/form")
    public FormDto getForm() {
        return new FormDto(
                1,
                "What is your favourite programming language?",
                "multiple_choice",
                List.of("Java", "JavaScript", "Python", "C#")
        );
    }
}
