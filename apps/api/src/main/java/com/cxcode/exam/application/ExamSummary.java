package com.cxcode.exam.application;

import com.cxcode.exam.domain.ExamStatus;

import java.time.Instant;

public record ExamSummary(
        String id,
        String title,
        ExamStatus status,
        Instant startAt,
        Instant endAt,
        int durationMinutes
) {
}

