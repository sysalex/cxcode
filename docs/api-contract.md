# API 契约规范

## 基础规则

- API 必须在实现前或实现时同步文档化。
- 请求和响应必须有类型或 schema 校验。
- 除非 ADR 明确批准，API 变更必须保持向后兼容。
- 所有写操作必须经过认证和授权。
- 提交、自动保存、超时提交、成绩发布接口必须幂等。

## 响应格式

成功响应：

```json
{
  "success": true,
  "data": {},
  "requestId": "req_123"
}
```

错误响应：

```json
{
  "success": false,
  "error": {
    "code": "EXAM_ALREADY_SUBMITTED",
    "message": "该作答已经提交。",
    "requestId": "req_123",
    "details": {}
  }
}
```

说明：`message` 可以在前端国际化，但 `code` 必须稳定。

## HTTP 状态码映射

- `400`：输入格式错误
- `401`：未登录或身份无效
- `403`：无权限
- `404`：资源不存在或有意隐藏
- `409`：业务状态冲突
- `422`：语义校验失败
- `429`：请求过于频繁
- `500`：未预期服务端错误

## 幂等要求

以下写接口必须接收或派生幂等键：

- `POST /attempts/{attemptId}/answers:auto-save`
- `POST /attempts/{attemptId}:submit`
- `POST /attempts/{attemptId}:timeout-submit`
- `POST /grades/{gradeId}:publish`

相同幂等键的重试必须返回相同的业务结果。

## 版本策略

- 公共 API 使用 `/api/v1`。
- 删除字段前必须先废弃。
- 错误码属于稳定 API 表面。
- 关键前端流程必须有 API 契约测试。
