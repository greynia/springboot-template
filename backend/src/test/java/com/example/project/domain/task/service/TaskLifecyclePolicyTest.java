package com.example.project.domain.task.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.project.application.exception.ForbiddenApplicationException;
import com.example.project.common.enums.UserRole;
import org.junit.jupiter.api.Test;

class TaskLifecyclePolicyTest {

    private final TaskLifecyclePolicy policy = new TaskLifecyclePolicy();

    @Test
    void shouldAllowAdminToTransitionAnyTask() {
        assertDoesNotThrow(() -> policy.assertCanTransition(1L, UserRole.ADMIN, 2L));
    }

    @Test
    void shouldAllowAssigneeToTransitionOwnTask() {
        assertDoesNotThrow(() -> policy.assertCanTransition(2L, UserRole.USER, 2L));
    }

    @Test
    void shouldRejectNonAssigneeUserTransition() {
        assertThrows(
                ForbiddenApplicationException.class,
                () -> policy.assertCanTransition(3L, UserRole.USER, 2L)
        );
    }
}
