# 在线考试系统 Agent 工作指南

本仓库采用 Harness Engineering 工作方式。仓库内的文档、脚本、测试和质量门禁是项目事实源，不以聊天记录作为长期事实源。

## 开始任何任务前必须阅读

按需阅读，不要一次性读无关文件：

1. `docs/README.md`
2. `docs/context.md`
3. `ARCHITECTURE.md`
4. 与当前任务相关的领域、接口、安全、数据、测试、任务文档

如果任务涉及考试核心流程，还必须阅读：

1. `docs/domain-model.md`
2. `docs/state-machines.md`
3. `docs/security.md`
4. `docs/observability.md`

## 不可违反的规则

- 架构边界必须能被代码、lint、测试或 CI 机制验证。
- 不允许为了完成任务跳过质量门禁。
- 不允许为了让检查通过而删除测试或降低断言强度。
- 不允许引入隐藏的跨模块依赖。
- 涉及安全、权限、考试提交、自动保存、成绩发布的改动必须有测试。
- 修改领域行为时，必须同步更新领域文档和测试。
- 考试开始、结束、超时、提交判断以服务端时间为准。
- 答案提交、自动保存、超时提交、成绩发布必须具备幂等性。
- 考试生命周期关键动作必须记录结构化日志和审计事件。
- 重要架构决策必须新增或更新 ADR。

## 标准任务循环

1. 阅读任务，识别影响范围。
2. 按 `docs/definition-of-ready.md` 检查任务是否可开始。
3. 使用 `docs/tasks/TEMPLATE.md` 创建或更新任务计划。
4. 做最小且完整的改动。
5. 按 `docs/project-scripts.md` 运行本地质量门禁。
6. 如果门禁失败，定位原因、修复、重新运行失败项。
7. 行为变化时同步更新文档、ADR、API 契约和测试。
8. 按 `docs/definition-of-done.md` 检查是否完成。

## 标准脚本入口

不要猜命令，优先使用这些脚本：

- `scripts/setup.ps1`
- `scripts/dev.ps1`
- `scripts/check.ps1`
- `scripts/test.ps1`
- `scripts/db-reset.ps1`

技术栈确定后，这些脚本需要接入真实的依赖安装、服务启动、数据库、测试和构建命令。

