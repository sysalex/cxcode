package com.cxcode.exam.application;

import com.cxcode.exam.application.port.AuditLogger;
import com.cxcode.exam.application.port.ExamStore;
import com.cxcode.exam.domain.Answer;
import com.cxcode.exam.domain.AnswerSheet;
import com.cxcode.exam.domain.Attempt;
import com.cxcode.exam.domain.DomainException;
import com.cxcode.exam.domain.Exam;
import com.cxcode.exam.domain.ExamDomainService;
import com.cxcode.exam.domain.Paper;
import com.cxcode.exam.domain.Question;
import com.cxcode.exam.domain.SubmitKind;
import com.cxcode.exam.domain.SubmissionOutcome;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

public class ExamApplicationService {
    private final ExamStore store;
    private final Clock clock;
    private final AuditLogger auditLogger;

    public ExamApplicationService(ExamStore store, Clock clock, AuditLogger auditLogger) {
        this.store = store;
        this.clock = clock;
        this.auditLogger = auditLogger;
    }

    public CandidateExamList listCandidateExams(String candidateId) {
        List<ExamSummary> exams = store.listExamsForCandidate(candidateId).stream()
                .map(exam -> new ExamSummary(
                        exam.id(),
                        exam.title(),
                        exam.status(),
                        exam.startAt(),
                        exam.endAt(),
                        exam.durationMinutes()
                ))
                .toList();
        return new CandidateExamList(exams, Instant.now(clock));
    }

    public AttemptView startOrResumeAttempt(String examId, String candidateId) {
        return store.findAttemptByExamAndCandidate(examId, candidateId)
                .map(bundle -> {
                    recordAudit("attempt.resumed", candidateId, bundle.exam().id(), bundle.attempt().id(), "success", null);
                    return toAttemptView(bundle);
                })
                .orElseGet(() -> startNewAttempt(examId, candidateId));
    }

    public AttemptView getAttempt(String attemptId, String candidateId) {
        AttemptBundle bundle = requireAttemptBundle(attemptId);
        ensureCandidateOwnsAttempt(bundle.attempt(), candidateId);
        return toAttemptView(bundle);
    }

    public AttemptView autoSave(String attemptId, String candidateId, long clientRevision, List<Answer> answers) {
        AttemptBundle bundle = requireAttemptBundle(attemptId);
        ensureCandidateOwnsAttempt(bundle.attempt(), candidateId);
        Instant now = Instant.now(clock);
        try {
            AnswerSheet saved = ExamDomainService.autoSave(
                    bundle.attempt(),
                    bundle.answerSheet(),
                    clientRevision,
                    answers,
                    now
            );
            store.saveAnswerSheet(saved);
            recordAudit("answer.auto_saved", candidateId, bundle.exam().id(), bundle.attempt().id(), "success", null);
            return toAttemptView(new AttemptBundle(
                    bundle.exam(),
                    bundle.paper(),
                    bundle.questions(),
                    bundle.attempt(),
                    saved
            ));
        } catch (DomainException error) {
            recordAudit("answer.save_failed", candidateId, bundle.exam().id(), bundle.attempt().id(), "failure", error.code());
            throw error;
        }
    }

    public AttemptView submit(String attemptId, String candidateId, String idempotencyKey, SubmitKind kind) {
        String scopedKey = attemptId + ":" + idempotencyKey;
        return store.getSubmissionByIdempotencyKey(scopedKey)
                .map(existing -> {
                    AttemptBundle bundle = requireAttemptBundle(attemptId);
                    ensureCandidateOwnsAttempt(bundle.attempt(), candidateId);
                    return toAttemptView(new AttemptBundle(
                            bundle.exam(),
                            bundle.paper(),
                            bundle.questions(),
                            existing.attempt(),
                            existing.answerSheet()
                    ));
                })
                .orElseGet(() -> submitFirstTime(attemptId, candidateId, scopedKey, kind));
    }

    private AttemptView startNewAttempt(String examId, String candidateId) {
        Exam exam = store.getExam(examId)
                .orElseThrow(() -> new ApplicationException("EXAM_NOT_FOUND", "考试不存在。"));
        Paper paper = store.getPaper(exam.paperId())
                .orElseThrow(() -> new ApplicationException("PAPER_NOT_FOUND", "试卷不存在。"));
        List<Question> questions = store.getQuestionsByVersionIds(
                paper.questions().stream().map(item -> item.questionVersionId()).toList()
        );
        Instant now = Instant.now(clock);
        Attempt attempt = ExamDomainService.createAttempt(store.nextId("attempt"), exam, candidateId, now);
        AnswerSheet answerSheet = ExamDomainService.createAnswerSheet(store.nextId("answer-sheet"), attempt.id(), now);
        AttemptBundle bundle = new AttemptBundle(exam, paper, questions, attempt, answerSheet);
        store.createAttemptBundle(bundle);
        recordAudit("attempt.created", candidateId, exam.id(), attempt.id(), "success", null);
        return toAttemptView(bundle);
    }

    private AttemptView submitFirstTime(
            String attemptId,
            String candidateId,
            String scopedKey,
            SubmitKind kind
    ) {
        AttemptBundle bundle = requireAttemptBundle(attemptId);
        ensureCandidateOwnsAttempt(bundle.attempt(), candidateId);
        SubmissionOutcome outcome = ExamDomainService.submit(
                bundle.attempt(),
                bundle.answerSheet(),
                kind,
                Instant.now(clock)
        );
        store.saveSubmission(outcome);
        store.saveSubmissionByIdempotencyKey(scopedKey, outcome);
        recordAudit(outcome.eventName(), candidateId, bundle.exam().id(), bundle.attempt().id(), "success", null);
        return toAttemptView(new AttemptBundle(
                bundle.exam(),
                bundle.paper(),
                bundle.questions(),
                outcome.attempt(),
                outcome.answerSheet()
        ));
    }

    private AttemptBundle requireAttemptBundle(String attemptId) {
        return store.getAttemptBundle(attemptId)
                .orElseThrow(() -> new ApplicationException("ATTEMPT_NOT_FOUND", "作答不存在。"));
    }

    private void ensureCandidateOwnsAttempt(Attempt attempt, String candidateId) {
        if (!attempt.candidateId().equals(candidateId)) {
            throw new ApplicationException("PERMISSION_DENIED", "无权访问该作答。");
        }
    }

    private AttemptView toAttemptView(AttemptBundle bundle) {
        return new AttemptView(
                bundle.attempt(),
                bundle.answerSheet(),
                ExamDomainService.toCandidatePaper(bundle.paper(), bundle.questions()),
                Instant.now(clock)
        );
    }

    private void recordAudit(
            String event,
            String userId,
            String examId,
            String attemptId,
            String result,
            String errorCode
    ) {
        auditLogger.record(new AuditEvent(
                event,
                userId,
                examId,
                attemptId,
                result,
                errorCode,
                Instant.now(clock)
        ));
    }
}

