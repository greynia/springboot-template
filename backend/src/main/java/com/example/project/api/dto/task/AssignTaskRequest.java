package com.example.project.api.dto.task;

import jakarta.validation.constraints.NotNull;

public record AssignTaskRequest(
        @NotNull Long assigneeId
) {
}
