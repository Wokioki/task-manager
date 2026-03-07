package com.wokioki.server.dto.task;

import jakarta.validation.constraints.NotBlank;

public record TaskCreateRequest(
        @NotBlank(message = "Title is required") String title,
        String description
) {}