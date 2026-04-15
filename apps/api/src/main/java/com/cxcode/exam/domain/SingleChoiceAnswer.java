package com.cxcode.exam.domain;

public record SingleChoiceAnswer(String questionId, String selectedOptionId) implements Answer {
    @Override
    public QuestionType type() {
        return QuestionType.SINGLE_CHOICE;
    }
}

