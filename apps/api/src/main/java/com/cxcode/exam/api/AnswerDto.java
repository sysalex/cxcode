package com.cxcode.exam.api;

import com.cxcode.exam.domain.Answer;
import com.cxcode.exam.domain.SingleChoiceAnswer;
import com.cxcode.exam.domain.ShortTextAnswer;
import jakarta.validation.constraints.NotBlank;

public record AnswerDto(
        @NotBlank String questionId,
        @NotBlank String type,
        String selectedOptionId,
        String text
) {
    public Answer toDomain() {
        return switch (type) {
            case "single_choice", "SINGLE_CHOICE" -> new SingleChoiceAnswer(questionId, selectedOptionId);
            case "short_text", "SHORT_TEXT" -> new ShortTextAnswer(questionId, text == null ? "" : text);
            default -> throw new IllegalArgumentException("不支持的答案类型：" + type);
        };
    }
}

