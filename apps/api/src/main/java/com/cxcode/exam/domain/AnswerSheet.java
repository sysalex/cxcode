package com.cxcode.exam.domain;

import java.time.Instant;
import java.util.List;

public record AnswerSheet(
        String id,
        String attemptId,
        AnswerSheetStatus status,
        List<Answer> answers,
        long revision,
        Instant updatedAt,
        Instant submittedAt
) {
}

