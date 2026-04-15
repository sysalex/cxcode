# 状态机规范

状态流转必须显式定义、可测试、可审计。

## 考试状态

```text
DRAFT -> SCHEDULED -> LIVE -> CLOSED -> ARCHIVED
DRAFT -> CANCELLED
SCHEDULED -> CANCELLED
LIVE -> CANCELLED
```

规则：

- 只有 `SCHEDULED` 状态的考试可以进入 `LIVE`。
- `LIVE` 状态的考试不能接受不安全的配置变更。
- `CANCELLED` 和 `ARCHIVED` 是正常流程下的终态。

## 作答状态

```text
NOT_STARTED -> IN_PROGRESS -> SUBMITTED
NOT_STARTED -> IN_PROGRESS -> TIMEOUT_SUBMITTED
IN_PROGRESS -> INVALIDATED
IN_PROGRESS -> ABANDONED
```

规则：

- `SUBMITTED`、`TIMEOUT_SUBMITTED`、`INVALIDATED`、`ABANDONED` 是终态。
- 手动提交和超时提交并发时，必须收敛到一个安全结果。
- 恢复作答不能创建重复的进行中作答。

## 答卷状态

```text
DRAFT -> SUBMITTED -> LOCKED
DRAFT -> TIMEOUT_SUBMITTED -> LOCKED
```

规则：

- `DRAFT` 可以自动保存。
- `SUBMITTED` 和 `TIMEOUT_SUBMITTED` 不能再编辑。
- `LOCKED` 表示答卷进入阅卷或审计保护状态。

## 阅卷状态

```text
PENDING -> AUTO_GRADED -> REVIEW_REQUIRED -> REVIEWED -> FINALIZED
PENDING -> REVIEW_REQUIRED
AUTO_GRADED -> FINALIZED
FINALIZED -> CORRECTED
```

规则：

- 成绩修正必须记录原因和操作人。
- 考生可见结果必须来自 `FINALIZED` 或 `CORRECTED` 状态。

## 成绩发布状态

```text
HIDDEN -> PUBLISHED -> REVOKED
PUBLISHED -> CORRECTED -> PUBLISHED
```

规则：

- 成绩发布必须可审计。
- 撤回和修正必须有原因码。
