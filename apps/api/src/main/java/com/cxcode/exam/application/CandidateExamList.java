package com.cxcode.exam.application;

import java.time.Instant;
import java.util.List;

public record CandidateExamList(List<ExamSummary> exams, Instant serverNow) {
}

