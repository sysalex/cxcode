package com.cxcode.exam.infrastructure;

import com.cxcode.exam.ExamApiApplication;
import com.cxcode.exam.application.AttemptView;
import com.cxcode.exam.application.CandidateExamList;
import com.cxcode.exam.application.ExamApplicationService;
import com.cxcode.exam.application.port.ExamStore;
import com.cxcode.exam.domain.AnswerSheetStatus;
import com.cxcode.exam.domain.AttemptStatus;
import com.cxcode.exam.domain.ShortTextAnswer;
import com.cxcode.exam.domain.SingleChoiceAnswer;
import com.cxcode.exam.domain.SubmitKind;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ExamApiApplication.class)
@ActiveProfiles("mysql")
class MysqlExamStoreIntegrationTest {
    @Autowired
    private ExamStore store;

    @Autowired
    private ExamApplicationService service;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanMutableTables() {
        jdbcTemplate.update("DELETE FROM submission_idempotency_records");
        jdbcTemplate.update("DELETE FROM answer_items");
        jdbcTemplate.update("DELETE FROM answer_sheets");
        jdbcTemplate.update("DELETE FROM attempts");
    }

    @Test
    void mysqlProfilePersistsExamAttemptAnswersAndIdempotentSubmission() {
        Class<?> storeClass = AopUtils.getTargetClass(store);
        assertThat(storeClass.getSimpleName()).isEqualTo("MysqlExamStore");

        CandidateExamList examList = service.listCandidateExams("candidate-1");
        assertThat(examList.exams()).extracting("id").containsExactly("exam-demo-1");

        AttemptView started = service.startOrResumeAttempt("exam-demo-1", "candidate-1");
        assertThat(started.attempt().status()).isEqualTo(AttemptStatus.IN_PROGRESS);

        AttemptView resumed = service.startOrResumeAttempt("exam-demo-1", "candidate-1");
        assertThat(resumed.attempt().id()).isEqualTo(started.attempt().id());

        AttemptView saved = service.autoSave(
                started.attempt().id(),
                "candidate-1",
                0,
                List.of(
                        new SingleChoiceAnswer("qv-demo-1", "b"),
                        new ShortTextAnswer("qv-demo-3", "保留本地草稿并安全重试。")
                )
        );
        assertThat(saved.answerSheet().revision()).isEqualTo(1);
        assertThat(saved.answerSheet().answers()).hasSize(2);

        AttemptView submitted = service.submit(
                started.attempt().id(),
                "candidate-1",
                "submit-key-1",
                SubmitKind.MANUAL
        );
        assertThat(submitted.attempt().status()).isEqualTo(AttemptStatus.SUBMITTED);
        assertThat(submitted.answerSheet().status()).isEqualTo(AnswerSheetStatus.SUBMITTED);

        AttemptView repeated = service.submit(
                started.attempt().id(),
                "candidate-1",
                "submit-key-1",
                SubmitKind.MANUAL
        );
        assertThat(repeated.attempt().submittedAt()).isEqualTo(submitted.attempt().submittedAt());
        assertThat(repeated.answerSheet().submittedAt()).isEqualTo(submitted.answerSheet().submittedAt());
    }
}
