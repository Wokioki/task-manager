package com.wokioki.server.controller;


import com.wokioki.server.dto.TaskCreateRequest;
import com.wokioki.server.dto.TaskResponse;
import com.wokioki.server.dto.TaskUpdateRequest;
import com.wokioki.server.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public List<TaskResponse> getAll() {
        return taskService.findAll();
    }

    @PostMapping
    public TaskResponse create(@Valid @RequestBody TaskCreateRequest req) {
        return taskService.create(req);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> update(@PathVariable Long id, @Valid @RequestBody TaskUpdateRequest req) {
        return taskService.update(id, req)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return taskService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public TaskResponse getOne(@PathVariable Long id){
        return taskService.findById(id);
    }
}