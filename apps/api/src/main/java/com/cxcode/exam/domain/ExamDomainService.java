package com.cxcode.exam.domain;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class ExamDomainService {
    private ExamDomainService() {
    }

    public static void ensureExamCanBeStarted(Exam exam, String candidateId, Instant now) {
        if (!exam.candidateIds().contains(candidateId)) {
            throw new DomainException("PERMISSION_DENIED", "考生无权参加该考试。", Map.of(
                    "examId", exam.id(),
                    "candidateId", candidateId
            ));
        }
        if (exam.status() != ExamStatus.LIVE) {
            throw new DomainException("EXAM_NOT_OPEN", "考试尚未开放。", Map.of(
                    "examId", exam.id(),
                    "status", exam.status().name()
            ));
        }
        if (now.isBefore(exam.startAt())) {
            throw new DomainException("EXAM_NOT_STARTED", "考试还未开始。", Map.of(
                    "examId", exam.id()
            ));
        }
        if (now.isAfter(exam.endAt())) {
            throw new DomainException("EXAM_ALREADY_CLOSED", "考试已经结束。", Map.of(
                    "examId", exam.id()
            ));
        }
    }

    public static Attempt createAttempt(String id, Exam exam, String candidateId, Instant now) {
        ensureExamCanBeStarted(exam, candidateId, now);
        Instant durationEnd = now.plus(exam.durationMinutes(), ChronoUnit.MINUTES);
        Instant endsAt = durationEnd.isBefore(exam.endAt()) ? durationEnd : exam.endAt();
        return new Attempt(
                id,
                exam.id(),
                candidateId,
                AttemptStatus.IN_PROGRESS,
                now,
                endsAt,
                null,
                1
        );
    }

    public static AnswerSheet createAnswerSheet(String id, String attemptId, Instant now) {
        return new AnswerSheet(
                id,
                attemptId,
                AnswerSheetStatus.DRAFT,
                List.of(),
                0,
                now,
                null
        );
    }

    public static AnswerSheet autoSave(
            Attempt attempt,
            AnswerSheet answerSheet,
            long clientRevision,
            List<Answer> answers,
            Instant now
    ) {
        ensureAttemptCanBeEdited(attempt, answerSheet);
        if (clientRevision < answerSheet.revision()) {
            throw new DomainException("ANSWER_REVISION_CONFLICT", "答案版本已过期，请刷新后重试。", Map.of(
                    "attemptId", attempt.id(),
                    "clientRevision", clientRevision,
                    "serverRevision", answerSheet.revision()
            ));
        }
        return new AnswerSheet(
                answerSheet.id(),
                answerSheet.attemptId(),
                answerSheet.status(),
                List.copyOf(answers),
                answerSheet.revision() + 1,
                now,
                answerSheet.submittedAt()
        );
    }

    public static SubmissionOutcome submit(
            Attempt attempt,
            AnswerSheet answerSheet,
            SubmitKind kind,
            Instant now
    ) {
        if (attempt.status() == AttemptStatus.SUBMITTED
                || attempt.status() == AttemptStatus.TIMEOUT_SUBMITTED) {
            String eventName = attempt.status() == AttemptStatus.SUBMITTED
                    ? "exam.submitted"
                    : "exam.timeout_submitted";
            return new SubmissionOutcome(attempt, answerSheet, eventName);
        }

        ensureAttemptCanBeEdited(attempt, answerSheet);
        boolean timeout = kind == SubmitKind.TIMEOUT || !now.isBefore(attempt.endsAt());
        AttemptStatus attemptStatus = timeout ? AttemptStatus.TIMEOUT_SUBMITTED : AttemptStatus.SUBMITTED;
        AnswerSheetStatus sheetStatus = timeout ? AnswerSheetStatus.TIMEOUT_SUBMITTED : AnswerSheetStatus.SUBMITTED;
        Attempt submittedAttempt = new Attempt(
                attempt.id(),
                attempt.examId(),
                attempt.candidateId(),
                attemptStatus,
                attempt.startedAt(),
                attempt.endsAt(),
                now,
                attempt.version() + 1
        );
        AnswerSheet submittedSheet = new AnswerSheet(
                answerSheet.id(),
                answerSheet.attemptId(),
                sheetStatus,
                answerSheet.answers(),
                answerSheet.revision(),
                now,
                now
        );
        return new SubmissionOutcome(
                submittedAttempt,
                submittedSheet,
                timeout ? "exam.timeout_submitted" : "exam.submitted"
        );
    }

    public static CandidatePaper toCandidatePaper(Paper paper, List<Question> questions) {
        List<CandidateQuestion> candidateQuestions = new ArrayList<>();
        for (PaperQuestion paperQuestion : paper.questions().stream()
                .sorted(Comparator.comparingInt(PaperQuestion::order))
                .toList()) {
            Question question = questions.stream()
                    .filter(item -> item.versionId().equals(paperQuestion.questionVersionId()))
                    .findFirst()
                    .orElseThrow(() -> new DomainException(
                            "QUESTION_VERSION_NOT_FOUND",
                            "试卷引用的试题版本不存在。",
                            Map.of("questionVersionId", paperQuestion.questionVersionId())
                    ));

            if (question instanceof SingleChoiceQuestion singleChoiceQuestion) {
                candidateQuestions.add(new CandidateQuestion(
                        singleChoiceQuestion.id(),
                        singleChoiceQuestion.versionId(),
                        singleChoiceQuestion.type(),
                        singleChoiceQuestion.prompt(),
                        singleChoiceQuestion.points(),
                        singleChoiceQuestion.options(),
                        null
                ));
            } else if (question instanceof ShortTextQuestion shortTextQuestion) {
                candidateQuestions.add(new CandidateQuestion(
                        shortTextQuestion.id(),
                        shortTextQuestion.versionId(),
                        shortTextQuestion.type(),
                        shortTextQuestion.prompt(),
                        shortTextQuestion.points(),
                        List.of(),
                        shortTextQuestion.maxLength()
                ));
            }
        }
        return new CandidatePaper(paper.id(), paper.title(), candidateQuestions);
    }

    private static void ensureAttemptCanBeEdited(Attempt attempt, AnswerSheet answerSheet) {
        if (attempt.status() != AttemptStatus.IN_PROGRESS || answerSheet.status() != AnswerSheetStatus.DRAFT) {
            throw new DomainException("ANSWER_SHEET_LOCKED", "答卷已锁定，不能继续修改。", Map.of(
                    "attemptId", attempt.id(),
                    "attemptStatus", attempt.status().name(),
                    "answerSheetStatus", answerSheet.status().name()
            ));
        }
    }
}

