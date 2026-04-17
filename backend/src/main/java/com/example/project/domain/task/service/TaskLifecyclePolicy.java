package com.example.project.domain.task.service;

import com.example.project.application.exception.ForbiddenApplicationException;
import com.example.project.application.exception.ResourceNotFoundApplicationException;
import com.example.project.common.enums.UserRole;
import com.example.project.infrastructure.persistence.jpa.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskLifecyclePolicy {

    public void assertAdmin(UserRole role) {
        if (role != UserRole.ADMIN) {
            throw new ForbiddenApplicationException("TASK_ASSIGN_FORBIDDEN", "Only admins can assign tasks.");
        }
    }

    public void assertCanTransition(Long actorId, UserRole actorRole, Long assigneeId) {
        if (actorRole == UserRole.ADMIN) {
            return;
        }
        if (assigneeId == null || !assigneeId.equals(actorId)) {
            throw new ForbiddenApplicationException("TASK_TRANSITION_FORBIDDEN", "Only the assignee or an admin can change task status.");
        }
    }

    public void assertAssignableUser(UserEntity assignee) {
        if (assignee == null) {
            throw new ResourceNotFoundApplicationException("ASSIGNEE_NOT_FOUND", "Assignee was not found.");
        }
        if (!assignee.isActive()) {
            throw new ForbiddenApplicationException("ASSIGNEE_INACTIVE", "Inactive users cannot be assigned tasks.");
        }
    }
}
