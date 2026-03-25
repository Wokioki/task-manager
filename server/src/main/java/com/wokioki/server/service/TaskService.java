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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public Page<TaskResponse> findAll(String email, Boolean done, String q, Pageable pageable) {
        User user = getUserByEmail(email);
        Long userId = user.getId();

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

    public TaskResponse findById(String email, Long id) {
        User user = getUserByEmail(email);

        Task task = taskRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new TaskNotFoundException(id));

        return TaskMapper.toResponse(task);
    }

    public TaskResponse create(String email, TaskCreateRequest req) {
        User user = getUserByEmail(email);

        Task task = new Task();
        task.setTitle(req.title());
        task.setDescription(req.description());
        task.setDone(false);
        task.setUser(user);

        return TaskMapper.toResponse(taskRepository.save(task));
    }

    public TaskResponse update(String email, Long id, TaskUpdateRequest req) {
        User user = getUserByEmail(email);

        Task existing = taskRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new TaskNotFoundException(id));

        existing.setTitle(req.title());
        existing.setDescription(req.description());
        existing.setDone(req.done());

        return TaskMapper.toResponse(taskRepository.save(existing));
    }

    public void delete(String email, Long id) {
        User user = getUserByEmail(email);

        Task task = taskRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new TaskNotFoundException(id));

        taskRepository.delete(task);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}