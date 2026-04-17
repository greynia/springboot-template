package com.example.project.application.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.example.project.api.dto.task.CreateTaskRequest;
import com.example.project.application.exception.BadRequestApplicationException;
import com.example.project.application.exception.ForbiddenApplicationException;
import com.example.project.common.enums.TaskStatus;
import com.example.project.common.enums.UserRole;
import com.example.project.domain.task.service.TaskLifecyclePolicy;
import com.example.project.infrastructure.persistence.jpa.entity.TaskEntity;
import com.example.project.infrastructure.persistence.jpa.repository.TaskRepository;
import com.example.project.infrastructure.persistence.jpa.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskApplicationServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskLifecyclePolicy taskLifecyclePolicy;

    private TaskMapper taskMapper;

    @InjectMocks
    private TaskApplicationService taskApplicationService;

    @BeforeEach
    void setUp() {
        taskMapper = new TaskMapper();
        taskApplicationService = new TaskApplicationService(taskRepository, userRepository, taskMapper, taskLifecyclePolicy);
    }

    @Test
    void shouldCreateTodoTask() {
        when(taskRepository.save(any(TaskEntity.class))).thenAnswer(invocation -> {
            TaskEntity entity = invocation.getArgument(0);
            entity.setId(10L);
            return entity;
        });

        var response = taskApplicationService.createTask(new CreateTaskRequest("Write docs", "Finish README", null));

        assertEquals(10L, response.id());
        assertEquals(TaskStatus.TODO, response.status());
        assertEquals("Write docs", response.title());
    }

    @Test
    void shouldRejectBlankTitle() {
        assertThrows(
                BadRequestApplicationException.class,
                () -> taskApplicationService.createTask(new CreateTaskRequest(" ", "desc", null))
        );
    }

    @Test
    void shouldRequireAdminForAssignment() {
        doThrow(new ForbiddenApplicationException("TASK_ASSIGN_FORBIDDEN", "Only admins can assign tasks."))
                .when(taskLifecyclePolicy)
                .assertAdmin(UserRole.USER);

        assertThrows(
                ForbiddenApplicationException.class,
                () -> taskApplicationService.assignTask(1L, 2L, new com.example.project.application.auth.AuthenticatedUser(5L, "user@example.com", "User", UserRole.USER))
        );
    }
}
