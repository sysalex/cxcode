package com.cxcode.exam.domain;

import java.time.Instant;

public record Attempt(
        String id,
        String examId,
        String candidateId,
        AttemptStatus status,
        Instant startedAt,
        Instant endsAt,
        Instant submittedAt,
        long version
) {
}

