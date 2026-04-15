package com.cxcode.exam.domain;

public sealed interface Question permits SingleChoiceQuestion, ShortTextQuestion {
    String id();

    String versionId();

    QuestionType type();

    String prompt();

    int points();
}

