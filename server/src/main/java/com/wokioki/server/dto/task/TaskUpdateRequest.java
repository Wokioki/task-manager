package com.wokioki.server.dto.task;

import jakarta.validation.constraints.NotBlank;

public record TaskUpdateRequest(
        @NotBlank String title,
        String description,
        boolean done
){}
