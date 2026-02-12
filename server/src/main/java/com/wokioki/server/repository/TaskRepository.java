package com.wokioki.server.repository;

import com.wokioki.server.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByDone(boolean done, Pageable pageable);

    Page<Task> findByTitleContainingIgnoreCase(String q, Pageable pageable);

}
