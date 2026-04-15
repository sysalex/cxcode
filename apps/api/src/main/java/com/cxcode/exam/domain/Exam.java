package com.cxcode.exam.domain;

import java.time.Instant;
import java.util.Set;

public record Exam(
        String id,
        String title,
        ExamStatus status,
        Instant startAt,
        Instant endAt,
        int durationMinutes,
        String paperId,
        Set<String> candidateIds
) {
}

