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

//    @PostMapping("/answer")
//    public ResponseEntity<?> submitAnswer(@RequestBody Answer_dto answer) { // "Takes the Json from the frontend and converts it into an Anser_dto object"
//        System.out.println("Received answer: " + answer); //just printing/logging it out for now.
//        return ResponseEntity.ok().build(); // This is the Http request im sending back to the React hopefully. means Status= 200 Ok.
//    }
}
