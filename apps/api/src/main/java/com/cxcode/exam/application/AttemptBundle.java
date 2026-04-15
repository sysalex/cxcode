package com.cxcode.exam.application;

import com.cxcode.exam.domain.AnswerSheet;
import com.cxcode.exam.domain.Attempt;
import com.cxcode.exam.domain.Exam;
import com.cxcode.exam.domain.Paper;
import com.cxcode.exam.domain.Question;

import java.util.List;

public record AttemptBundle(
        Exam exam,
        Paper paper,
        List<Question> questions,
        Attempt attempt,
        AnswerSheet answerSheet
) {
}

