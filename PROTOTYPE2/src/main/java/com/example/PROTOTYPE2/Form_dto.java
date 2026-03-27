package com.example.PROTOTYPE2;

import java.util.List;

public record Form_dto(
        Integer id,
        String prompt,
        String type,          // "text" or "multiple_choice"
        List<String> options  // null/empty if type="text"
) {}
