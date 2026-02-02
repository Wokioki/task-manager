package com.wokioki.server.mapper;

import com.wokioki.server.dto.TaskResponse;
import com.wokioki.server.model.Task;

public class TaskMapper {

    public static TaskResponse toResponse(Task task){
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .done(task.isDone())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

}
