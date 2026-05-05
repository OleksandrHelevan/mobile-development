package com.clinicapp.api.dto;

public record NoteResponse(
        String id,
        String title,
        String content,
        int priority,
        String category,
        boolean isFavorite,
        int estimatedTime,
        String sourceUrl
) {}