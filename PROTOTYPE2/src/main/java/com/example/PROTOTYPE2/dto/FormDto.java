package com.example.PROTOTYPE2.dto;

import java.util.List;

public record FormDto(
        Integer id,
        String prompt,
        String type,          // "text" or "multiple_choice"
        List<String> options  // null/empty if type="text"
) {}
