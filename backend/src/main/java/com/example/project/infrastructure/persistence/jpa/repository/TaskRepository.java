package com.example.project.infrastructure.persistence.jpa.repository;

import com.example.project.infrastructure.persistence.jpa.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
}
