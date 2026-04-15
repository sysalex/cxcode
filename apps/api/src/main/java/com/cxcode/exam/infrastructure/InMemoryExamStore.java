package com.cxcode.exam.infrastructure;

import com.cxcode.exam.application.AttemptBundle;
import com.cxcode.exam.application.port.ExamStore;
import com.cxcode.exam.domain.AnswerSheet;
import com.cxcode.exam.domain.Attempt;
import com.cxcode.exam.domain.Exam;
import com.cxcode.exam.domain.Paper;
import com.cxcode.exam.domain.Question;
import com.cxcode.exam.domain.SubmissionOutcome;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryExamStore implements ExamStore {
    private final Map<String, Exam> exams = new HashMap<>();
    private final Map<String, Paper> papers = new HashMap<>();
    private final Map<String, Question> questions = new HashMap<>();
    private final Map<String, Attempt> attempts = new HashMap<>();
    private final Map<String, AnswerSheet> answerSheets = new HashMap<>();
    private final Map<String, SubmissionOutcome> submissions = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong();

    public InMemoryExamStore(SeedData seedData) {
        seedData.exams().forEach(exam -> exams.put(exam.id(), exam));
        seedData.papers().forEach(paper -> papers.put(paper.id(), paper));
        seedData.questions().forEach(question -> questions.put(question.versionId(), question));
    }

    @Override
    public List<Exam> listExamsForCandidate(String candidateId) {
        return exams.values().stream()
                .filter(exam -> exam.candidateIds().contains(candidateId))
                .sorted(Comparator.comparing(Exam::startAt))
                .toList();
    }

    @Override
    public Optional<Exam> getExam(String examId) {
        return Optional.ofNullable(exams.get(examId));
    }

    @Override
    public Optional<Paper> getPaper(String paperId) {
        return Optional.ofNullable(papers.get(paperId));
    }

    @Override
    public List<Question> getQuestionsByVersionIds(List<String> questionVersionIds) {
        List<Question> result = new ArrayList<>();
        for (String questionVersionId : questionVersionIds) {
            Question question = questions.get(questionVersionId);
            if (question != null) {
                result.add(question);
            }
        }
        return result;
    }

    @Override
    public Optional<AttemptBundle> findAttemptByExamAndCandidate(String examId, String candidateId) {
        return attempts.values().stream()
                .filter(attempt -> attempt.examId().equals(examId))
                .filter(attempt -> attempt.candidateId().equals(candidateId))
                .findFirst()
                .flatMap(attempt -> getAttemptBundle(attempt.id()));
    }

    @Override
    public Optional<AttemptBundle> getAttemptBundle(String attemptId) {
        Attempt attempt = attempts.get(attemptId);
        if (attempt == null) {
            return Optional.empty();
        }

        AnswerSheet answerSheet = answerSheets.values().stream()
                .filter(item -> item.attemptId().equals(attemptId))
                .findFirst()
                .orElse(null);
        if (answerSheet == null) {
            return Optional.empty();
        }

        Exam exam = exams.get(attempt.examId());
        if (exam == null) {
            return Optional.empty();
        }

        Paper paper = papers.get(exam.paperId());
        if (paper == null) {
            return Optional.empty();
        }

        List<Question> paperQuestions = getQuestionsByVersionIds(
                paper.questions().stream().map(item -> item.questionVersionId()).toList()
        );
        return Optional.of(new AttemptBundle(exam, paper, paperQuestions, attempt, answerSheet));
    }

    @Override
    public void createAttemptBundle(AttemptBundle bundle) {
        attempts.put(bundle.attempt().id(), bundle.attempt());
        answerSheets.put(bundle.answerSheet().id(), bundle.answerSheet());
    }

    @Override
    public void saveAnswerSheet(AnswerSheet answerSheet) {
        answerSheets.put(answerSheet.id(), answerSheet);
    }

    @Override
    public void saveSubmission(SubmissionOutcome outcome) {
        attempts.put(outcome.attempt().id(), outcome.attempt());
        answerSheets.put(outcome.answerSheet().id(), outcome.answerSheet());
    }

    @Override
    public Optional<SubmissionOutcome> getSubmissionByIdempotencyKey(String key) {
        return Optional.ofNullable(submissions.get(key));
    }

    @Override
    public void saveSubmissionByIdempotencyKey(String key, SubmissionOutcome outcome) {
        submissions.put(key, outcome);
    }

    @Override
    public String nextId(String prefix) {
        return prefix + "-" + idCounter.incrementAndGet();
    }
}

