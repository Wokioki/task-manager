package com.wokioki.server.service;

import com.wokioki.server.dto.TaskCreateRequest;
import com.wokioki.server.dto.TaskResponse;
import com.wokioki.server.dto.TaskUpdateRequest;
import com.wokioki.server.model.Task;
import com.wokioki.server.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public List<TaskResponse> findAll() {
        return taskRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public TaskResponse create(TaskCreateRequest req) {
        Task task = new Task();
        task.setTitle(req.title());
        task.setDescription(req.description());
        task.setDone(false);

        return toResponse(taskRepository.save(task));
    }

    public Optional<TaskResponse> update(Long id, TaskUpdateRequest req) {
        return taskRepository.findById(id).map(existing -> {
            existing.setTitle(req.title());
            existing.setDescription(req.description());
            existing.setDone(req.done());
            return toResponse(taskRepository.save(existing));
        });
    }

    public boolean delete(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private TaskResponse toResponse(Task task){
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.isDone()
        );
    }
}
