package com.wokioki.server.service;

import com.wokioki.server.dto.TaskCreateRequest;
import com.wokioki.server.dto.TaskResponse;
import com.wokioki.server.dto.TaskUpdateRequest;
import com.wokioki.server.exception.TaskNotFoundException;
import com.wokioki.server.mapper.TaskMapper;
import com.wokioki.server.model.Task;
import com.wokioki.server.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public TaskResponse update(Long id, TaskUpdateRequest req) {
        Task existing = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        existing.setTitle(req.title());
        existing.setDescription(req.description());
        existing.setDone(req.done());

        Task saved = taskRepository.save(existing);
        return TaskMapper.toResponse(saved);
    }


    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        taskRepository.deleteById(id);
    }

    public Page<TaskResponse> findAll(Boolean done, String q, Pageable pageable) {

        Page<Task> page;

        if(q != null && !q.isBlank()) {
            page= taskRepository.findByTitleContainingIgnoreCase(q.trim(), pageable);
        }else if (done != null) {
            page = taskRepository.findByDone(done, pageable);
        }else{
            page = taskRepository.findAll(pageable);
        }

        return page.map(TaskMapper::toResponse);
    }
}
