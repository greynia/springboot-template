package com.example.project.application.task;

import com.example.project.api.dto.common.UserSummaryResponse;
import com.example.project.api.dto.task.TaskResponse;
import com.example.project.application.auth.AuthenticatedUser;
import com.example.project.infrastructure.persistence.jpa.entity.TaskEntity;
import com.example.project.infrastructure.persistence.jpa.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskResponse toResponse(TaskEntity task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                toUserSummary(task.getAssignee()),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

    public UserSummaryResponse toUserSummary(UserEntity user) {
        if (user == null) {
            return null;
        }
        return new UserSummaryResponse(user.getId(), user.getEmail(), user.getName(), user.getRole());
    }

    public UserSummaryResponse toUserSummary(AuthenticatedUser user) {
        return new UserSummaryResponse(user.id(), user.email(), user.name(), user.role());
    }
}
