package com.cxcode.exam.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AutoSaveRequest(
        @Min(0) long clientRevision,
        @NotNull List<@Valid AnswerDto> answers
) {
}

