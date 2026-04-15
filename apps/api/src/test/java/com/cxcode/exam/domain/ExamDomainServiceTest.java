package com.cxcode.exam.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExamDomainServiceTest {
    private final Exam liveExam = new Exam(
            "exam-1",
            "入学摸底考试",
            ExamStatus.LIVE,
            Instant.parse("2026-04-15T01:00:00Z"),
            Instant.parse("2026-04-15T03:00:00Z"),
            60,
            "paper-1",
            Set.of("candidate-1")
    );

    @Test
    void rejectsUnassignedCandidate() {
        assertThatThrownBy(() -> ExamDomainService.ensureExamCanBeStarted(
                liveExam,
                "candidate-2",
                Instant.parse("2026-04-15T01:30:00Z")
        )).isInstanceOf(DomainException.class);
    }

    @Test
    void createsAttemptWithEarlierEndTime() {
        Attempt attempt = ExamDomainService.createAttempt(
                "attempt-1",
                liveExam,
                "candidate-1",
                Instant.parse("2026-04-15T02:30:00Z")
        );

        assertThat(attempt.endsAt()).isEqualTo(Instant.parse("2026-04-15T03:00:00Z"));
    }

    @Test
    void autoSaveIncreasesRevision() {
        Attempt attempt = ExamDomainService.createAttempt(
                "attempt-1",
                liveExam,
                "candidate-1",
                Instant.parse("2026-04-15T01:30:00Z")
        );
        AnswerSheet sheet = ExamDomainService.createAnswerSheet(
                "sheet-1",
                attempt.id(),
                Instant.parse("2026-04-15T01:30:00Z")
        );

        AnswerSheet saved = ExamDomainService.autoSave(
                attempt,
                sheet,
                0,
                List.of(new SingleChoiceAnswer("qv-1", "a")),
                Instant.parse("2026-04-15T01:31:00Z")
        );

        assertThat(saved.revision()).isEqualTo(1);
    }

    @Test
    void locksAnswerSheetAfterSubmit() {
        Attempt attempt = ExamDomainService.createAttempt(
                "attempt-1",
                liveExam,
                "candidate-1",
                Instant.parse("2026-04-15T01:30:00Z")
        );
        AnswerSheet sheet = ExamDomainService.createAnswerSheet(
                "sheet-1",
                attempt.id(),
                Instant.parse("2026-04-15T01:30:00Z")
        );
        SubmissionOutcome outcome = ExamDomainService.submit(
                attempt,
                sheet,
                SubmitKind.MANUAL,
                Instant.parse("2026-04-15T01:40:00Z")
        );

        assertThat(outcome.attempt().status()).isEqualTo(AttemptStatus.SUBMITTED);
        assertThatThrownBy(() -> ExamDomainService.autoSave(
                outcome.attempt(),
                outcome.answerSheet(),
                outcome.answerSheet().revision(),
                List.of(),
                Instant.parse("2026-04-15T01:41:00Z")
        )).isInstanceOf(DomainException.class);
    }
}

