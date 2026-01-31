package com.wokioki.server.service;

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

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Task create(Task task) {
        task.setId(null);
        return taskRepository.save(task);
    }

    public Optional<Task> update(Long id, Task details) {
        return taskRepository.findById(id).map(existing -> {
            existing.setTitle(details.getTitle());
            existing.setDescription(details.getDescription());
            existing.setDone(details.isDone());
            return taskRepository.save(existing);
        });
    }

    public boolean delete(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
