package com.wokioki.server.dto;

public record TaskResponse (
        Long id,
        String title,
        String description,
        boolean done
){}
