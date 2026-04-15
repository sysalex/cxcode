# 错误与异常体系

## 异常分类

- `DomainException`：违反业务不变量。
- `ValidationException`：系统边界输入无效。
- `AuthenticationException`：未登录或身份无效。
- `AuthorizationException`：已登录但无权限。
- `ConflictException`：请求与当前业务状态冲突。
- `IdempotencyException`：幂等键冲突或重放不一致。
- `NotFoundException`：资源不存在或对当前用户隐藏。
- `RateLimitException`：请求超过限制。
- `ExternalServiceException`：外部服务失败。
- `InfrastructureException`：数据库、队列、缓存、存储等基础设施失败。
- `UnexpectedException`：未分类的系统缺陷。

## 错误码规则

- 使用大写蛇形命名。
- 必要时增加领域前缀。
- 发布后保持稳定。
- 不暴露内部实现细节。

示例：

```text
EXAM_NOT_OPEN
EXAM_ALREADY_SUBMITTED
ATTEMPT_NOT_FOUND
ANSWER_SHEET_LOCKED
QUESTION_VERSION_IMMUTABLE
RESULT_NOT_PUBLISHED
PERMISSION_DENIED
IDEMPOTENCY_CONFLICT
```

## 恢复规则

- 自动保存失败时，只要作答未终态，就应支持安全重试。
- 提交失败必须可安全重试。
- 超时提交和手动提交并发时必须收敛。
- 基础设施失败必须记录关联字段，并向用户返回安全错误信息。
