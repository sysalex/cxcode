package com.cxcode.exam.application;

import java.time.Instant;

public record AuditEvent(
        String event,
        String userId,
        String examId,
        String attemptId,
        String result,
        String errorCode,
        Instant occurredAt
) {
}

