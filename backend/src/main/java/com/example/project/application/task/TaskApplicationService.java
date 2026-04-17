package com.example.project.application.task;

import com.example.project.api.dto.task.CreateTaskRequest;
import com.example.project.api.dto.task.TaskResponse;
import com.example.project.application.auth.AuthenticatedUser;
import com.example.project.application.exception.BadRequestApplicationException;
import com.example.project.application.exception.ResourceNotFoundApplicationException;
import com.example.project.domain.task.exception.TaskDomainException;
import com.example.project.domain.task.model.Task;
import com.example.project.domain.task.service.TaskLifecyclePolicy;
import com.example.project.infrastructure.persistence.jpa.entity.TaskEntity;
import com.example.project.infrastructure.persistence.jpa.entity.UserEntity;
import com.example.project.infrastructure.persistence.jpa.repository.TaskRepository;
import com.example.project.infrastructure.persistence.jpa.repository.UserRepository;
import java.time.OffsetDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskApplicationService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;
    private final TaskLifecyclePolicy taskLifecyclePolicy;

    public TaskApplicationService(
            TaskRepository taskRepository,
            UserRepository userRepository,
            TaskMapper taskMapper,
            TaskLifecyclePolicy taskLifecyclePolicy
    ) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskMapper = taskMapper;
        this.taskLifecyclePolicy = taskLifecyclePolicy;
    }

    @Transactional
    public TaskResponse createTask(CreateTaskRequest request) {
        UserEntity assignee = null;
        if (request.assigneeId() != null) {
            assignee = userRepository.findById(request.assigneeId()).orElse(null);
            taskLifecyclePolicy.assertAssignableUser(assignee);
        }

        Task draft = handleDomain(() -> Task.createDraft(request.title(), request.description(), request.assigneeId()));

        TaskEntity entity = new TaskEntity();
        entity.setTitle(draft.getTitle());
        entity.setDescription(draft.getDescription());
        entity.setStatus(draft.getStatus());
        entity.setAssignee(assignee);
        entity.setCreatedAt(OffsetDateTime.now());
        entity.setUpdatedAt(OffsetDateTime.now());

        return taskMapper.toResponse(taskRepository.save(entity));
    }

    @Transactional
    public TaskResponse assignTask(Long taskId, Long assigneeId, AuthenticatedUser actor) {
        taskLifecyclePolicy.assertAdmin(actor.role());
        TaskEntity entity = getTaskEntity(taskId);
        UserEntity assignee = userRepository.findById(assigneeId).orElse(null);
        taskLifecyclePolicy.assertAssignableUser(assignee);

        Task updated = handleDomain(() -> toDomain(entity).assign(assigneeId));
        apply(entity, updated, assignee);
        return taskMapper.toResponse(taskRepository.save(entity));
    }

    @Transactional
    public TaskResponse startTask(Long taskId, AuthenticatedUser actor) {
        return transition(taskId, actor, Action.START);
    }

    @Transactional
    public TaskResponse completeTask(Long taskId, AuthenticatedUser actor) {
        return transition(taskId, actor, Action.COMPLETE);
    }

    @Transactional
    public TaskResponse cancelTask(Long taskId, AuthenticatedUser actor) {
        return transition(taskId, actor, Action.CANCEL);
    }

    private TaskResponse transition(Long taskId, AuthenticatedUser actor, Action action) {
        TaskEntity entity = getTaskEntity(taskId);
        Long assigneeId = entity.getAssignee() != null ? entity.getAssignee().getId() : null;
        taskLifecyclePolicy.assertCanTransition(actor.id(), actor.role(), assigneeId);

        Task updated = switch (action) {
            case START -> handleDomain(() -> toDomain(entity).start());
            case COMPLETE -> handleDomain(() -> toDomain(entity).complete());
            case CANCEL -> handleDomain(() -> toDomain(entity).cancel());
        };

        apply(entity, updated, entity.getAssignee());
        return taskMapper.toResponse(taskRepository.save(entity));
    }

    private TaskEntity getTaskEntity(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundApplicationException("TASK_NOT_FOUND", "Task was not found."));
    }

    private Task toDomain(TaskEntity entity) {
        return new Task(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getAssignee() != null ? entity.getAssignee().getId() : null
        );
    }

    private void apply(TaskEntity entity, Task task, UserEntity assignee) {
        entity.setTitle(task.getTitle());
        entity.setDescription(task.getDescription());
        entity.setStatus(task.getStatus());
        entity.setAssignee(assignee);
        entity.setUpdatedAt(OffsetDateTime.now());
    }

    private Task handleDomain(TaskSupplier supplier) {
        try {
            return supplier.get();
        } catch (TaskDomainException ex) {
            throw new BadRequestApplicationException("TASK_RULE_VIOLATION", ex.getMessage());
        }
    }

    private enum Action {
        START,
        COMPLETE,
        CANCEL
    }

    @FunctionalInterface
    private interface TaskSupplier {
        Task get();
    }
}
