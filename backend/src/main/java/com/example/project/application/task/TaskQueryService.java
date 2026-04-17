package com.example.project.application.task;

import com.example.project.api.dto.common.PageResponse;
import com.example.project.api.dto.task.TaskResponse;
import com.example.project.application.exception.ResourceNotFoundApplicationException;
import com.example.project.infrastructure.persistence.jpa.entity.TaskEntity;
import com.example.project.infrastructure.persistence.jpa.repository.TaskRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskQueryService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskQueryService(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    @Transactional(readOnly = true)
    public PageResponse<TaskResponse> listTasks() {
        var tasks = taskRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .map(taskMapper::toResponse)
                .toList();
        return new PageResponse<>(tasks, tasks.size());
    }

    @Transactional(readOnly = true)
    public TaskResponse getTask(Long taskId) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundApplicationException("TASK_NOT_FOUND", "Task was not found."));
        return taskMapper.toResponse(task);
    }
}
