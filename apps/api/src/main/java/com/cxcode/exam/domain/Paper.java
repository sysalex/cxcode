package com.cxcode.exam.domain;

import java.time.Instant;
import java.util.List;

public record Paper(
        String id,
        String title,
        List<PaperQuestion> questions,
        Instant assignedAt
) {
}

