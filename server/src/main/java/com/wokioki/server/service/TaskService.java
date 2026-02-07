package com.wokioki.server.service;

import com.wokioki.server.dto.TaskCreateRequest;
import com.wokioki.server.dto.TaskResponse;
import com.wokioki.server.dto.TaskUpdateRequest;
import com.wokioki.server.exception.TaskNotFoundException;
import com.wokioki.server.mapper.TaskMapper;
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
        return taskRepository.findAll()
                .stream()
                .map(TaskMapper::toResponse)
                .toList();
    }

    public TaskResponse findById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        return TaskMapper.toResponse(task);
    }

    public TaskResponse create(TaskCreateRequest req) {
        Task task = new Task();
        task.setTitle(req.title());
        task.setDescription(req.description());
        task.setDone(false);

        return TaskMapper.toResponse(taskRepository.save(task));
    }

    public Optional<TaskResponse> update(Long id, TaskUpdateRequest req) {
        return taskRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(req.title());
                    existing.setDescription(req.description());
                    existing.setDone(req.done());
                    return TaskMapper.toResponse(taskRepository.save(existing));
        });
    }

    public boolean delete(Long id) {
        if (!taskRepository.existsById(id)) {
            return false;
        }
        taskRepository.deleteById(id);
        return true;
    }
}
