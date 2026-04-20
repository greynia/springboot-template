package com.example.project.api.dto.task;

import com.example.project.api.dto.common.AuditActorResponse;
import com.example.project.api.dto.common.UserSummaryResponse;
import com.example.project.common.enums.TaskStatus;
import java.time.OffsetDateTime;

public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        UserSummaryResponse assignee,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        AuditActorResponse createdBy
) {
}
