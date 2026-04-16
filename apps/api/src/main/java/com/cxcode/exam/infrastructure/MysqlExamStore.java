package com.cxcode.exam.infrastructure;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cxcode.exam.application.AttemptBundle;
import com.cxcode.exam.application.port.ExamStore;
import com.cxcode.exam.domain.Answer;
import com.cxcode.exam.domain.AnswerSheet;
import com.cxcode.exam.domain.AnswerSheetStatus;
import com.cxcode.exam.domain.Attempt;
import com.cxcode.exam.domain.AttemptStatus;
import com.cxcode.exam.domain.Exam;
import com.cxcode.exam.domain.ExamStatus;
import com.cxcode.exam.domain.Paper;
import com.cxcode.exam.domain.PaperQuestion;
import com.cxcode.exam.domain.Question;
import com.cxcode.exam.domain.QuestionOption;
import com.cxcode.exam.domain.QuestionType;
import com.cxcode.exam.domain.ShortTextAnswer;
import com.cxcode.exam.domain.ShortTextQuestion;
import com.cxcode.exam.domain.SingleChoiceAnswer;
import com.cxcode.exam.domain.SingleChoiceQuestion;
import com.cxcode.exam.domain.SubmissionOutcome;
import com.cxcode.exam.infrastructure.persistence.entity.AnswerItemEntity;
import com.cxcode.exam.infrastructure.persistence.entity.AnswerSheetEntity;
import com.cxcode.exam.infrastructure.persistence.entity.AttemptEntity;
import com.cxcode.exam.infrastructure.persistence.entity.ExamCandidateEntity;
import com.cxcode.exam.infrastructure.persistence.entity.ExamEntity;
import com.cxcode.exam.infrastructure.persistence.entity.PaperEntity;
import com.cxcode.exam.infrastructure.persistence.entity.PaperQuestionEntity;
import com.cxcode.exam.infrastructure.persistence.entity.QuestionEntity;
import com.cxcode.exam.infrastructure.persistence.entity.QuestionOptionEntity;
import com.cxcode.exam.infrastructure.persistence.entity.SubmissionIdempotencyRecordEntity;
import com.cxcode.exam.infrastructure.persistence.mapper.AnswerItemMapper;
import com.cxcode.exam.infrastructure.persistence.mapper.AnswerSheetMapper;
import com.cxcode.exam.infrastructure.persistence.mapper.AttemptMapper;
import com.cxcode.exam.infrastructure.persistence.mapper.ExamCandidateMapper;
import com.cxcode.exam.infrastructure.persistence.mapper.ExamMapper;
import com.cxcode.exam.infrastructure.persistence.mapper.PaperMapper;
import com.cxcode.exam.infrastructure.persistence.mapper.PaperQuestionMapper;
import com.cxcode.exam.infrastructure.persistence.mapper.QuestionMapper;
import com.cxcode.exam.infrastructure.persistence.mapper.QuestionOptionMapper;
import com.cxcode.exam.infrastructure.persistence.mapper.SubmissionIdempotencyRecordMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@Profile("mysql")
@Transactional
public class MysqlExamStore implements ExamStore {
    private final ExamMapper examMapper;
    private final ExamCandidateMapper examCandidateMapper;
    private final PaperMapper paperMapper;
    private final PaperQuestionMapper paperQuestionMapper;
    private final QuestionMapper questionMapper;
    private final QuestionOptionMapper questionOptionMapper;
    private final AttemptMapper attemptMapper;
    private final AnswerSheetMapper answerSheetMapper;
    private final AnswerItemMapper answerItemMapper;
    private final SubmissionIdempotencyRecordMapper submissionMapper;

    public MysqlExamStore(
            ExamMapper examMapper,
            ExamCandidateMapper examCandidateMapper,
            PaperMapper paperMapper,
            PaperQuestionMapper paperQuestionMapper,
            QuestionMapper questionMapper,
            QuestionOptionMapper questionOptionMapper,
            AttemptMapper attemptMapper,
            AnswerSheetMapper answerSheetMapper,
            AnswerItemMapper answerItemMapper,
            SubmissionIdempotencyRecordMapper submissionMapper
    ) {
        this.examMapper = examMapper;
        this.examCandidateMapper = examCandidateMapper;
        this.paperMapper = paperMapper;
        this.paperQuestionMapper = paperQuestionMapper;
        this.questionMapper = questionMapper;
        this.questionOptionMapper = questionOptionMapper;
        this.attemptMapper = attemptMapper;
        this.answerSheetMapper = answerSheetMapper;
        this.answerItemMapper = answerItemMapper;
        this.submissionMapper = submissionMapper;
    }

    @Override
    public List<Exam> listExamsForCandidate(String candidateId) {
        return examCandidateMapper.selectList(Wrappers.<ExamCandidateEntity>lambdaQuery()
                        .eq(ExamCandidateEntity::getCandidateId, candidateId))
                .stream()
                .map(ExamCandidateEntity::getExamId)
                .distinct()
                .map(examMapper::selectById)
                .filter(entity -> entity != null)
                .map(this::toExam)
                .sorted(Comparator.comparing(Exam::startAt))
                .toList();
    }

    @Override
    public Optional<Exam> getExam(String examId) {
        return Optional.ofNullable(examMapper.selectById(examId)).map(this::toExam);
    }

    @Override
    public Optional<Paper> getPaper(String paperId) {
        return Optional.ofNullable(paperMapper.selectById(paperId)).map(this::toPaper);
    }

    @Override
    public List<Question> getQuestionsByVersionIds(List<String> questionVersionIds) {
        return questionVersionIds.stream()
                .map(questionMapper::selectById)
                .filter(entity -> entity != null)
                .map(this::toQuestion)
                .toList();
    }

    @Override
    public Optional<AttemptBundle> findAttemptByExamAndCandidate(String examId, String candidateId) {
        AttemptEntity attempt = attemptMapper.selectOne(Wrappers.<AttemptEntity>lambdaQuery()
                .eq(AttemptEntity::getExamId, examId)
                .eq(AttemptEntity::getCandidateId, candidateId));
        if (attempt == null) {
            return Optional.empty();
        }
        return getAttemptBundle(attempt.getId());
    }

    @Override
    public Optional<AttemptBundle> getAttemptBundle(String attemptId) {
        AttemptEntity attemptEntity = attemptMapper.selectById(attemptId);
        if (attemptEntity == null) {
            return Optional.empty();
        }
        AnswerSheetEntity answerSheetEntity = answerSheetMapper.selectOne(Wrappers.<AnswerSheetEntity>lambdaQuery()
                .eq(AnswerSheetEntity::getAttemptId, attemptId));
        if (answerSheetEntity == null) {
            return Optional.empty();
        }
        Exam exam = getExam(attemptEntity.getExamId()).orElse(null);
        if (exam == null) {
            return Optional.empty();
        }
        Paper paper = getPaper(exam.paperId()).orElse(null);
        if (paper == null) {
            return Optional.empty();
        }
        List<Question> questions = getQuestionsByVersionIds(
                paper.questions().stream().map(PaperQuestion::questionVersionId).toList()
        );
        return Optional.of(new AttemptBundle(
                exam,
                paper,
                questions,
                toAttempt(attemptEntity),
                toAnswerSheet(answerSheetEntity)
        ));
    }

    @Override
    public void createAttemptBundle(AttemptBundle bundle) {
        attemptMapper.insert(toAttemptEntity(bundle.attempt()));
        answerSheetMapper.insert(toAnswerSheetEntity(bundle.answerSheet()));
        saveAnswerItems(bundle.answerSheet());
    }

    @Override
    public void saveAnswerSheet(AnswerSheet answerSheet) {
        answerSheetMapper.updateById(toAnswerSheetEntity(answerSheet));
        saveAnswerItems(answerSheet);
    }

    @Override
    public void saveSubmission(SubmissionOutcome outcome) {
        attemptMapper.updateById(toAttemptEntity(outcome.attempt()));
        saveAnswerSheet(outcome.answerSheet());
    }

    @Override
    public Optional<SubmissionOutcome> getSubmissionByIdempotencyKey(String key) {
        SubmissionIdempotencyRecordEntity record = submissionMapper.selectById(key);
        if (record == null) {
            return Optional.empty();
        }
        return getAttemptBundle(record.getAttemptId())
                .map(bundle -> new SubmissionOutcome(bundle.attempt(), bundle.answerSheet(), record.getEventName()));
    }

    @Override
    public void saveSubmissionByIdempotencyKey(String key, SubmissionOutcome outcome) {
        SubmissionIdempotencyRecordEntity entity = new SubmissionIdempotencyRecordEntity();
        entity.setIdempotencyKey(key);
        entity.setAttemptId(outcome.attempt().id());
        entity.setEventName(outcome.eventName());
        entity.setCreatedAt(Instant.now());
        submissionMapper.insert(entity);
    }

    @Override
    public String nextId(String prefix) {
        return prefix + "-" + UUID.randomUUID();
    }

    public void seedIfEmpty(SeedData seedData) {
        if (examMapper.selectCount(null) > 0) {
            return;
        }
        seedData.papers().forEach(this::insertPaper);
        seedData.questions().forEach(this::insertQuestion);
        seedData.exams().forEach(this::insertExam);
    }

    private void insertExam(Exam exam) {
        examMapper.insert(toExamEntity(exam));
        for (String candidateId : exam.candidateIds()) {
            ExamCandidateEntity entity = new ExamCandidateEntity();
            entity.setId(exam.id() + ":" + candidateId);
            entity.setExamId(exam.id());
            entity.setCandidateId(candidateId);
            entity.setCreatedAt(Instant.now());
            examCandidateMapper.insert(entity);
        }
    }

    private void insertPaper(Paper paper) {
        paperMapper.insert(toPaperEntity(paper));
        for (PaperQuestion paperQuestion : paper.questions()) {
            PaperQuestionEntity entity = new PaperQuestionEntity();
            entity.setId(paper.id() + ":" + paperQuestion.questionVersionId());
            entity.setPaperId(paper.id());
            entity.setQuestionVersionId(paperQuestion.questionVersionId());
            entity.setQuestionOrder(paperQuestion.order());
            entity.setCreatedAt(Instant.now());
            paperQuestionMapper.insert(entity);
        }
    }

    private void insertQuestion(Question question) {
        questionMapper.insert(toQuestionEntity(question));
        if (question instanceof SingleChoiceQuestion singleChoiceQuestion) {
            int order = 1;
            for (QuestionOption option : singleChoiceQuestion.options()) {
                QuestionOptionEntity entity = new QuestionOptionEntity();
                entity.setId(singleChoiceQuestion.versionId() + ":" + option.id());
                entity.setQuestionVersionId(singleChoiceQuestion.versionId());
                entity.setOptionId(option.id());
                entity.setOptionLabel(option.label());
                entity.setOptionOrder(order++);
                entity.setCreatedAt(Instant.now());
                questionOptionMapper.insert(entity);
            }
        }
    }

    private Exam toExam(ExamEntity entity) {
        Set<String> candidateIds = examCandidateMapper.selectList(Wrappers.<ExamCandidateEntity>lambdaQuery()
                        .eq(ExamCandidateEntity::getExamId, entity.getId()))
                .stream()
                .map(ExamCandidateEntity::getCandidateId)
                .collect(Collectors.toSet());
        return new Exam(
                entity.getId(),
                entity.getTitle(),
                ExamStatus.valueOf(entity.getStatus()),
                entity.getStartAt(),
                entity.getEndAt(),
                entity.getDurationMinutes(),
                entity.getPaperId(),
                candidateIds
        );
    }

    private Paper toPaper(PaperEntity entity) {
        List<PaperQuestion> questions = paperQuestionMapper.selectList(Wrappers.<PaperQuestionEntity>lambdaQuery()
                        .eq(PaperQuestionEntity::getPaperId, entity.getId())
                        .orderByAsc(PaperQuestionEntity::getQuestionOrder))
                .stream()
                .map(item -> new PaperQuestion(item.getQuestionVersionId(), item.getQuestionOrder()))
                .toList();
        return new Paper(entity.getId(), entity.getTitle(), questions, entity.getAssignedAt());
    }

    private Question toQuestion(QuestionEntity entity) {
        QuestionType type = QuestionType.valueOf(entity.getType());
        if (type == QuestionType.SINGLE_CHOICE) {
            List<QuestionOption> options = questionOptionMapper.selectList(Wrappers.<QuestionOptionEntity>lambdaQuery()
                            .eq(QuestionOptionEntity::getQuestionVersionId, entity.getVersionId())
                            .orderByAsc(QuestionOptionEntity::getOptionOrder))
                    .stream()
                    .map(item -> new QuestionOption(item.getOptionId(), item.getOptionLabel()))
                    .toList();
            return new SingleChoiceQuestion(
                    entity.getQuestionId(),
                    entity.getVersionId(),
                    entity.getPrompt(),
                    entity.getPoints(),
                    options,
                    entity.getCorrectOptionId()
            );
        }
        return new ShortTextQuestion(
                entity.getQuestionId(),
                entity.getVersionId(),
                entity.getPrompt(),
                entity.getPoints(),
                entity.getMaxLength(),
                entity.getReferenceAnswer()
        );
    }

    private Attempt toAttempt(AttemptEntity entity) {
        return new Attempt(
                entity.getId(),
                entity.getExamId(),
                entity.getCandidateId(),
                AttemptStatus.valueOf(entity.getStatus()),
                entity.getStartedAt(),
                entity.getEndsAt(),
                entity.getSubmittedAt(),
                entity.getVersion()
        );
    }

    private AnswerSheet toAnswerSheet(AnswerSheetEntity entity) {
        List<Answer> answers = answerItemMapper.selectList(Wrappers.<AnswerItemEntity>lambdaQuery()
                        .eq(AnswerItemEntity::getAnswerSheetId, entity.getId())
                        .orderByAsc(AnswerItemEntity::getAnswerOrder))
                .stream()
                .map(this::toAnswer)
                .toList();
        return new AnswerSheet(
                entity.getId(),
                entity.getAttemptId(),
                AnswerSheetStatus.valueOf(entity.getStatus()),
                answers,
                entity.getRevision(),
                entity.getUpdatedAt(),
                entity.getSubmittedAt()
        );
    }

    private Answer toAnswer(AnswerItemEntity entity) {
        QuestionType type = QuestionType.valueOf(entity.getType());
        if (type == QuestionType.SINGLE_CHOICE) {
            return new SingleChoiceAnswer(entity.getQuestionId(), entity.getSelectedOptionId());
        }
        return new ShortTextAnswer(entity.getQuestionId(), entity.getTextAnswer() == null ? "" : entity.getTextAnswer());
    }

    private ExamEntity toExamEntity(Exam exam) {
        ExamEntity entity = new ExamEntity();
        entity.setId(exam.id());
        entity.setTitle(exam.title());
        entity.setStatus(exam.status().name());
        entity.setStartAt(exam.startAt());
        entity.setEndAt(exam.endAt());
        entity.setDurationMinutes(exam.durationMinutes());
        entity.setPaperId(exam.paperId());
        entity.setVersion(1L);
        return entity;
    }

    private PaperEntity toPaperEntity(Paper paper) {
        PaperEntity entity = new PaperEntity();
        entity.setId(paper.id());
        entity.setTitle(paper.title());
        entity.setAssignedAt(paper.assignedAt());
        entity.setVersion(1L);
        return entity;
    }

    private QuestionEntity toQuestionEntity(Question question) {
        QuestionEntity entity = new QuestionEntity();
        entity.setVersionId(question.versionId());
        entity.setQuestionId(question.id());
        entity.setType(question.type().name());
        entity.setPrompt(question.prompt());
        entity.setPoints(question.points());
        entity.setVersion(1L);
        if (question instanceof SingleChoiceQuestion singleChoiceQuestion) {
            entity.setCorrectOptionId(singleChoiceQuestion.correctOptionId());
        }
        if (question instanceof ShortTextQuestion shortTextQuestion) {
            entity.setMaxLength(shortTextQuestion.maxLength());
            entity.setReferenceAnswer(shortTextQuestion.referenceAnswer());
        }
        return entity;
    }

    private AttemptEntity toAttemptEntity(Attempt attempt) {
        AttemptEntity entity = new AttemptEntity();
        entity.setId(attempt.id());
        entity.setExamId(attempt.examId());
        entity.setCandidateId(attempt.candidateId());
        entity.setStatus(attempt.status().name());
        entity.setStartedAt(attempt.startedAt());
        entity.setEndsAt(attempt.endsAt());
        entity.setSubmittedAt(attempt.submittedAt());
        entity.setVersion(attempt.version());
        return entity;
    }

    private AnswerSheetEntity toAnswerSheetEntity(AnswerSheet answerSheet) {
        AnswerSheetEntity entity = new AnswerSheetEntity();
        entity.setId(answerSheet.id());
        entity.setAttemptId(answerSheet.attemptId());
        entity.setStatus(answerSheet.status().name());
        entity.setRevision(answerSheet.revision());
        entity.setUpdatedAt(answerSheet.updatedAt());
        entity.setSubmittedAt(answerSheet.submittedAt());
        entity.setVersion(1L);
        return entity;
    }

    private void saveAnswerItems(AnswerSheet answerSheet) {
        answerItemMapper.delete(Wrappers.<AnswerItemEntity>lambdaQuery()
                .eq(AnswerItemEntity::getAnswerSheetId, answerSheet.id()));
        int order = 1;
        for (Answer answer : answerSheet.answers()) {
            AnswerItemEntity entity = new AnswerItemEntity();
            entity.setId(answerSheet.id() + ":" + answer.questionId());
            entity.setAnswerSheetId(answerSheet.id());
            entity.setQuestionId(answer.questionId());
            entity.setType(answer.type().name());
            entity.setAnswerOrder(order++);
            entity.setCreatedAt(Instant.now());
            if (answer instanceof SingleChoiceAnswer singleChoiceAnswer) {
                entity.setSelectedOptionId(singleChoiceAnswer.selectedOptionId());
            }
            if (answer instanceof ShortTextAnswer shortTextAnswer) {
                entity.setTextAnswer(shortTextAnswer.text());
            }
            answerItemMapper.insert(entity);
        }
    }
}
