package com.task_management_system.REPOSITORY;

import com.task_management_system.ENTITY.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {
    List<Task> findAllByTitleContaining(String title);

    List<Task> findAllByUserId(Long id);
}
