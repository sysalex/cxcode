package com.cxcode.exam.domain;

public record ShortTextQuestion(
        String id,
        String versionId,
        String prompt,
        int points,
        int maxLength,
        String referenceAnswer
) implements Question {
    @Override
    public QuestionType type() {
        return QuestionType.SHORT_TEXT;
    }
}

