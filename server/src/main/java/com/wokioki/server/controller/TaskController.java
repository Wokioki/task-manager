package com.wokioki.server.controller;

import com.wokioki.server.dto.task.TaskCreateRequest;
import com.wokioki.server.dto.task.TaskResponse;
import com.wokioki.server.dto.task.TaskUpdateRequest;
import com.wokioki.server.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public Page<TaskResponse> getAll(
            Authentication authentication,
            @RequestParam(required = false) Boolean done,
            @RequestParam(required = false) String q,
            Pageable pageable
    ) {
        return taskService.findAll(authentication.getName(), done, q, pageable);
    }

    @GetMapping("/{id}")
    public TaskResponse getOne(Authentication authentication, @PathVariable Long id) {
        return taskService.findById(authentication.getName(), id);
    }

    @PostMapping
    public TaskResponse create(
            Authentication authentication,
            @Valid @RequestBody TaskCreateRequest req
    ) {
        return taskService.create(authentication.getName(), req);
    }

    @PutMapping("/{id}")
    public TaskResponse update(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody TaskUpdateRequest req
    ) {
        return taskService.update(authentication.getName(), id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Authentication authentication, @PathVariable Long id) {
        taskService.delete(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}