package com.cxcode.exam.infrastructure;

import com.cxcode.exam.domain.Exam;
import com.cxcode.exam.domain.ExamStatus;
import com.cxcode.exam.domain.Paper;
import com.cxcode.exam.domain.PaperQuestion;
import com.cxcode.exam.domain.Question;
import com.cxcode.exam.domain.QuestionOption;
import com.cxcode.exam.domain.ShortTextQuestion;
import com.cxcode.exam.domain.SingleChoiceQuestion;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

public final class SeedDataFactory {
    private SeedDataFactory() {
    }

    public static SeedData create(Clock clock) {
        Instant now = Instant.now(clock);
        Instant startAt = now.minus(1, ChronoUnit.HOURS);
        Instant endAt = now.plus(2, ChronoUnit.HOURS);

        List<Question> questions = List.of(
                new SingleChoiceQuestion(
                        "q-demo-1",
                        "qv-demo-1",
                        "在线考试中，判断考试是否超时应以哪个时间为准？",
                        5,
                        List.of(
                                new QuestionOption("a", "考生浏览器时间"),
                                new QuestionOption("b", "服务端时间"),
                                new QuestionOption("c", "教师电脑时间"),
                                new QuestionOption("d", "任意时间都可以")
                        ),
                        "b"
                ),
                new SingleChoiceQuestion(
                        "q-demo-2",
                        "qv-demo-2",
                        "重复点击交卷按钮时，系统最重要的行为是什么？",
                        5,
                        List.of(
                                new QuestionOption("a", "创建多份提交记录"),
                                new QuestionOption("b", "返回同一个业务结果"),
                                new QuestionOption("c", "清空答卷"),
                                new QuestionOption("d", "跳过权限检查")
                        ),
                        "b"
                ),
                new ShortTextQuestion(
                        "q-demo-3",
                        "qv-demo-3",
                        "请简述自动保存失败时，系统应该如何保证考生答案安全。",
                        10,
                        300,
                        "应提示用户、支持安全重试、保留本地草稿，并记录可观测日志。"
                )
        );

        Paper paper = new Paper(
                "paper-demo-1",
                "基础能力试卷",
                List.of(
                        new PaperQuestion("qv-demo-1", 1),
                        new PaperQuestion("qv-demo-2", 2),
                        new PaperQuestion("qv-demo-3", 3)
                ),
                now
        );

        Exam exam = new Exam(
                "exam-demo-1",
                "在线考试系统演示考试",
                ExamStatus.LIVE,
                startAt,
                endAt,
                90,
                paper.id(),
                Set.of("candidate-1")
        );

        return new SeedData(List.of(exam), List.of(paper), questions);
    }
}

