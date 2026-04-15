package com.cxcode.exam.domain;

import java.util.List;

public record CandidateQuestion(
        String id,
        String versionId,
        QuestionType type,
        String prompt,
        int points,
        List<QuestionOption> options,
        Integer maxLength
) {
}

