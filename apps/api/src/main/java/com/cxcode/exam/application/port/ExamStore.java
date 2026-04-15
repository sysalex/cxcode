package com.cxcode.exam.application.port;

import com.cxcode.exam.application.AttemptBundle;
import com.cxcode.exam.domain.AnswerSheet;
import com.cxcode.exam.domain.Exam;
import com.cxcode.exam.domain.Paper;
import com.cxcode.exam.domain.Question;
import com.cxcode.exam.domain.SubmissionOutcome;

import java.util.List;
import java.util.Optional;

public interface ExamStore {
    List<Exam> listExamsForCandidate(String candidateId);

    Optional<Exam> getExam(String examId);

    Optional<Paper> getPaper(String paperId);

    List<Question> getQuestionsByVersionIds(List<String> questionVersionIds);

    Optional<AttemptBundle> findAttemptByExamAndCandidate(String examId, String candidateId);

    Optional<AttemptBundle> getAttemptBundle(String attemptId);

    void createAttemptBundle(AttemptBundle bundle);

    void saveAnswerSheet(AnswerSheet answerSheet);

    void saveSubmission(SubmissionOutcome outcome);

    Optional<SubmissionOutcome> getSubmissionByIdempotencyKey(String key);

    void saveSubmissionByIdempotencyKey(String key, SubmissionOutcome outcome);

    String nextId(String prefix);
}

