package com.wokioki.server.dto;

import jakarta.validation.constraints.NotBlank;

public record TaskCreateRequest(
    @NotBlank String title,
    String description
){}
