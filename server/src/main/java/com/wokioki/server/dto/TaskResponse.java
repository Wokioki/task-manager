package com.wokioki.server.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private boolean done;
    private Instant createdAt;
    private Instant updatedAt;


}
