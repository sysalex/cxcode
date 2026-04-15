# 运行过程可观测

系统运行时必须能通过结构化日志、指标、链路追踪、审计事件、健康检查和告警进行诊断。

## 关联字段

在可获得时，日志和 trace 必须包含：

```text
timestamp
level
event
requestId
traceId
spanId
userId
role
organizationId
examId
attemptId
answerSheetId
paperId
questionId
durationMs
result
errorCode
```

## 结构化日志示例

```json
{
  "timestamp": "2026-04-15T10:30:00.000Z",
  "level": "info",
  "event": "exam.submitted",
  "requestId": "req_123",
  "traceId": "trace_456",
  "userId": "user_789",
  "examId": "exam_001",
  "attemptId": "attempt_001",
  "durationMs": 120,
  "result": "success"
}
```

## 必须采集的指标

- `exam.active_attempts`
- `exam.started.count`
- `answer.autosave.success.count`
- `answer.autosave.failure.count`
- `answer.autosave.latency.ms`
- `exam.submit.success.count`
- `exam.submit.failure.count`
- `exam.timeout_submit.count`
- `api.error.rate`
- `api.latency.ms`
- `db.query.latency.ms`
- `proctoring.violation.count`

## 告警

必须对以下情况建立告警：

- 自动保存失败率异常
- 提交失败率异常
- API 5xx 激增
- 数据库延迟异常
- 活跃考试服务不可用
- 审计事件写入失败

## 运行诊断能力

开发者和 Agent 必须能够：

- 本地启动系统。
- 创建测试考试数据。
- 执行关键考试流程。
- 按 `requestId` 或 `attemptId` 查询日志。
- 查看质量门禁失败输出。
- 按 runbook 复现故障。

