package com.wokioki.server.repository;

import com.wokioki.server.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByUserId(Long userId, Pageable pageable);

    Page<Task> findByUserIdAndDone(Long userId, boolean done, Pageable pageable);

    Page<Task> findByUserIdAndTitleContainingIgnoreCase(Long userId, String q, Pageable pageable);

    Optional<Task> findByIdAndUserId(Long id, Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);
}