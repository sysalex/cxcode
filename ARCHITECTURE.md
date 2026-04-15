# 架构约束

本文档定义在线考试系统的架构规则。规则不能只停留在文字层面，后续应尽量通过 lint、测试、CI 或代码生成进行机械化验证。

## 架构风格

采用以领域为中心的分层架构：

```text
domain -> application -> infrastructure
                         -> api
                         -> web
```

允许的依赖方向：

```text
domain          不依赖项目内其他层
application     依赖 domain
infrastructure  实现 application 端口，可依赖 domain/application
api             依赖 application 契约和 DTO
web             依赖生成的 API client 或明确的前端契约
shared          只放稳定的跨领域基础能力
```

禁止的依赖方向：

```text
domain -> application
domain -> infrastructure
domain -> api
domain -> web
application -> web
application -> api controller
infrastructure -> web UI
```

## 业务边界

主要领域边界：

- 身份与访问控制
- 考试管理
- 题库
- 组卷
- 考试作答
- 答卷
- 提交
- 阅卷
- 成绩发布
- 监考
- 审计
- 报表

每个领域边界应拥有自己的领域模型、应用服务、持久化映射、事件、测试和文档。

## 跨领域能力

以下能力必须通过明确接口注入，不允许领域代码直接调用具体框架或基础设施：

- 认证
- 授权
- 时钟
- ID 生成
- 日志
- 指标
- 链路追踪
- 审计日志
- Feature Flag
- 事务管理
- 通知

领域代码不得直接依赖数据库、HTTP、浏览器、系统时间、第三方 SDK 或 UI 框架。

## 状态与时间

- 考试开始、结束、超时、提交以服务端时间为准。
- 考试作答过程必须能容忍刷新页面和网络中断。
- 状态流转必须原子化并可审计。
- 自动保存、手动提交、超时提交必须具备幂等性。
- 已发布试题版本、已分配试卷、已提交答卷、审计日志、已发布成绩原则上不可变。

## 必须逐步落地的机械化检查

后续技术栈确定后，必须加入：

- 非法 import 检查
- 循环依赖检查
- API 契约漂移检查
- 关键领域规则测试缺失检查
- 非结构化日志检查
- 敏感操作缺少审计事件检查

这些检查应接入 `scripts/check.ps1` 和 CI。

