package com.wokioki.server.controller;


import com.wokioki.server.dto.task.TaskCreateRequest;
import com.wokioki.server.dto.task.TaskResponse;
import com.wokioki.server.dto.task.TaskUpdateRequest;
import com.wokioki.server.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public Page<TaskResponse> getAll(
            @RequestParam(required = false) Boolean done,
            @RequestParam(required = false) String q,
            Pageable pageable
    ) {
        return taskService.findAll(done, q, pageable);
    }

    @GetMapping("/{id}")
    public TaskResponse getOne(@PathVariable Long id){
        return taskService.findById(id);
    }

    @PostMapping
    public TaskResponse create(@Valid @RequestBody TaskCreateRequest req) {
        return taskService.create(req);
    }

    @PutMapping("/{id}")
    public TaskResponse update(@PathVariable Long id, @Valid @RequestBody TaskUpdateRequest req) {
        return taskService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }


}