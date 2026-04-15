package com.cxcode.exam.domain;

import java.util.List;

public record SingleChoiceQuestion(
        String id,
        String versionId,
        String prompt,
        int points,
        List<QuestionOption> options,
        String correctOptionId
) implements Question {
    @Override
    public QuestionType type() {
        return QuestionType.SINGLE_CHOICE;
    }
}

