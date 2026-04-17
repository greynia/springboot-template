package com.example.project.domain.task.model;

import com.example.project.common.enums.TaskStatus;
import com.example.project.domain.task.exception.TaskDomainException;

public class Task {

    private final Long id;
    private final String title;
    private final String description;
    private final TaskStatus status;
    private final Long assigneeId;

    public Task(Long id, String title, String description, TaskStatus status, Long assigneeId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.assigneeId = assigneeId;
    }

    public static Task createDraft(String title, String description, Long assigneeId) {
        validateTitle(title);
        return new Task(null, title.trim(), normalizeDescription(description), TaskStatus.TODO, assigneeId);
    }

    public Task start() {
        if (status != TaskStatus.TODO) {
            throw new TaskDomainException("Only TODO tasks can be started.");
        }
        return new Task(id, title, description, TaskStatus.IN_PROGRESS, assigneeId);
    }

    public Task complete() {
        if (status != TaskStatus.IN_PROGRESS) {
            throw new TaskDomainException("Only IN_PROGRESS tasks can be completed.");
        }
        return new Task(id, title, description, TaskStatus.DONE, assigneeId);
    }

    public Task cancel() {
        if (status == TaskStatus.DONE) {
            throw new TaskDomainException("Completed tasks cannot be cancelled.");
        }
        if (status == TaskStatus.CANCELLED) {
            throw new TaskDomainException("Cancelled tasks cannot be cancelled again.");
        }
        return new Task(id, title, description, TaskStatus.CANCELLED, assigneeId);
    }

    public Task assign(Long newAssigneeId) {
        if (status == TaskStatus.DONE || status == TaskStatus.CANCELLED) {
            throw new TaskDomainException("Terminal tasks cannot be reassigned.");
        }
        return new Task(id, title, description, status, newAssigneeId);
    }

    public static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new TaskDomainException("Task title must not be blank.");
        }
        if (title.trim().length() > 200) {
            throw new TaskDomainException("Task title must be 200 characters or fewer.");
        }
    }

    private static String normalizeDescription(String description) {
        return description == null || description.isBlank() ? null : description.trim();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }
}
