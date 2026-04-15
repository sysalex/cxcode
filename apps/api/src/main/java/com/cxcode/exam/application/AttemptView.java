package com.cxcode.exam.application;

import com.cxcode.exam.domain.AnswerSheet;
import com.cxcode.exam.domain.Attempt;
import com.cxcode.exam.domain.CandidatePaper;

import java.time.Instant;

public record AttemptView(
        Attempt attempt,
        AnswerSheet answerSheet,
        CandidatePaper paper,
        Instant serverNow
) {
}

