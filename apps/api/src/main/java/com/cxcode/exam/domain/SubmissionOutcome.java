package com.cxcode.exam.domain;

public record SubmissionOutcome(
        Attempt attempt,
        AnswerSheet answerSheet,
        String eventName
) {
}

