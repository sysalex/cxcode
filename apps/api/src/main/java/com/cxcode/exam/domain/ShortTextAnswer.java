package com.cxcode.exam.domain;

public record ShortTextAnswer(String questionId, String text) implements Answer {
    @Override
    public QuestionType type() {
        return QuestionType.SHORT_TEXT;
    }
}

