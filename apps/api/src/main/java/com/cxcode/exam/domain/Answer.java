package com.cxcode.exam.domain;

public sealed interface Answer permits SingleChoiceAnswer, ShortTextAnswer {
    String questionId();

    QuestionType type();
}

