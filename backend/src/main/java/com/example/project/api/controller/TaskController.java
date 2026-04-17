package com.example.project.api.controller;

import com.example.project.api.dto.common.PageResponse;
import com.example.project.api.dto.task.AssignTaskRequest;
import com.example.project.api.dto.task.CreateTaskRequest;
import com.example.project.api.dto.task.TaskResponse;
import com.example.project.application.auth.AuthenticatedUser;
import com.example.project.application.task.TaskApplicationService;
import com.example.project.application.task.TaskQueryService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskApplicationService taskApplicationService;
    private final TaskQueryService taskQueryService;

    public TaskController(TaskApplicationService taskApplicationService, TaskQueryService taskQueryService) {
        this.taskApplicationService = taskApplicationService;
        this.taskQueryService = taskQueryService;
    }

    @PostMapping
    public TaskResponse createTask(@Valid @RequestBody CreateTaskRequest request) {
        return taskApplicationService.createTask(request);
    }

    @GetMapping
    public PageResponse<TaskResponse> listTasks() {
        return taskQueryService.listTasks();
    }

    @GetMapping("/{taskId}")
    public TaskResponse getTask(@PathVariable Long taskId) {
        return taskQueryService.getTask(taskId);
    }

    @PatchMapping("/{taskId}/assign")
    public TaskResponse assignTask(
            @PathVariable Long taskId,
            @Valid @RequestBody AssignTaskRequest request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return taskApplicationService.assignTask(taskId, request.assigneeId(), authenticatedUser);
    }

    @PatchMapping("/{taskId}/start")
    public TaskResponse startTask(@PathVariable Long taskId, @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return taskApplicationService.startTask(taskId, authenticatedUser);
    }

    @PatchMapping("/{taskId}/complete")
    public TaskResponse completeTask(@PathVariable Long taskId, @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return taskApplicationService.completeTask(taskId, authenticatedUser);
    }

    @PatchMapping("/{taskId}/cancel")
    public TaskResponse cancelTask(@PathVariable Long taskId, @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return taskApplicationService.cancelTask(taskId, authenticatedUser);
    }
}
