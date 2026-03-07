package com.wokioki.server.service;

import com.wokioki.server.dto.task.TaskCreateRequest;
import com.wokioki.server.dto.task.TaskResponse;
import com.wokioki.server.dto.task.TaskUpdateRequest;
import com.wokioki.server.exception.TaskNotFoundException;
import com.wokioki.server.mapper.TaskMapper;
import com.wokioki.server.model.Task;
import com.wokioki.server.model.User;
import com.wokioki.server.repository.TaskRepository;
import com.wokioki.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public Page<TaskResponse> findAll(Long userId, Boolean done, String q, Pageable pageable) {
        Page<Task> page;

        if (q != null && !q.isBlank()) {
            page = taskRepository.findByUserIdAndTitleContainingIgnoreCase(userId, q.trim(), pageable);
        } else if (done != null) {
            page = taskRepository.findByUserIdAndDone(userId, done, pageable);
        } else {
            page = taskRepository.findByUserId(userId, pageable);
        }

        return page.map(TaskMapper::toResponse);
    }

    public TaskResponse findById(Long userId, Long id) {
        Task task = taskRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new TaskNotFoundException(id));

        return TaskMapper.toResponse(task);
    }

    public TaskResponse create(Long userId, TaskCreateRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Task task = new Task();
        task.setTitle(req.title());
        task.setDescription(req.description());
        task.setDone(false);
        task.setUser(user);

        return TaskMapper.toResponse(taskRepository.save(task));
    }

    public TaskResponse update(Long userId, Long id, TaskUpdateRequest req) {
        Task existing = taskRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new TaskNotFoundException(id));

        existing.setTitle(req.title());
        existing.setDescription(req.description());
        existing.setDone(req.done());

        return TaskMapper.toResponse(taskRepository.save(existing));
    }

    public void delete(Long userId, Long id) {
        Task task = taskRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new TaskNotFoundException(id));

        taskRepository.delete(task);
    }
}