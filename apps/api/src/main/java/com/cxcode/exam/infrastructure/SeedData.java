package com.cxcode.exam.infrastructure;

import com.cxcode.exam.domain.Exam;
import com.cxcode.exam.domain.Paper;
import com.cxcode.exam.domain.Question;

import java.util.List;

public record SeedData(
        List<Exam> exams,
        List<Paper> papers,
        List<Question> questions
) {
}

