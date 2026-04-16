# 数据模型与迁移规范

## 基础规则

- 数据结构变更必须通过 migration。
- migration 发布前必须评审。
- 破坏性 migration 必须有备份和回滚计划。
- 关键不可变记录不得硬删除。
- 敏感数据必须分类并保护。

## 标准字段

持久化业务记录建议包含：

```text
id
created_at
created_by
updated_at
updated_by
version
deleted_at
```

存在并发编辑风险的数据应使用 `version` 做乐观锁。

## 当前 MySQL Schema

阶段 2 使用 Flyway 管理 MySQL schema，migration 文件位于：

```text
apps/api/src/main/resources/db/migration/
```

当前第一版 migration：

```text
V1__create_exam_core_tables.sql
```

当前第二版 migration：

```text
V2__add_exam_core_table_comments.sql
```

V2 为阶段 2 核心表和字段补充 MySQL 注释。后续新增业务表或字段时，migration 必须同时写入 table comment 和 column comment，便于 Navicat、`information_schema`、排障和数据评审直接阅读。

覆盖考试作答 MVP 所需表：

- `exams`：考试基础信息、时间窗口、状态和关联试卷。
- `exam_candidates`：考试与考生分配关系。
- `papers`：已分配试卷。
- `paper_questions`：试卷内题目版本和顺序。
- `questions`：题目版本、题型、题干、分值和题型扩展字段。
- `question_options`：单选题选项。
- `attempts`：考生作答记录和作答状态。
- `answer_sheets`：答卷状态、版本和提交时间。
- `answer_items`：答卷内答案项。
- `submission_idempotency_records`：提交幂等键和业务结果引用。

本地 `mysql` profile 默认连接：

```text
jdbc:mysql://localhost:3306/cxcode_exam
username=${MYSQL_USERNAME:root}
password=${MYSQL_PASSWORD:}
```

## 不可变记录

以下记录最终确认后不可变：

- 已发布试题版本
- 已分配试卷
- 已提交答卷
- 审计事件
- 已发布成绩快照

修正历史数据时，应创建可审计的修正记录，而不是静默修改历史记录。

## 数据保留与恢复

生产前必须定义以下数据的保留策略：

- 答卷
- 审计日志
- 监考信号
- 用户会话
- 系统日志
- 备份

备份必须能在测试环境成功恢复。
