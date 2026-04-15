# 项目上下文

## 目标

建设一套 Web 版本在线考试系统，支持可靠考试交付、安全访问控制、答案持久化、阅卷、成绩发布、审计追踪和运行可观测。

## 项目事实源

- 架构规则：`ARCHITECTURE.md`
- 编码规则：`CODING_GUIDE.md`
- 领域行为：`docs/domain-model.md`
- 状态流转：`docs/state-machines.md`
- 产品流程：`docs/product-specs/online-exam-system.md`
- API 契约：`docs/api-contract.md`
- 安全与权限：`docs/security.md`
- 数据与迁移：`docs/data-model.md`
- 运行可观测：`docs/observability.md`
- 测试策略：`docs/testing.md`
- 质量门禁：`docs/quality-gates.md`
- 任务计划：`docs/tasks/`
- 架构决策：`docs/adr/`

## 当前假设

- 技术栈尚未确定。
- 第一版实现应让领域逻辑独立于具体框架。
- 系统需要支持浏览器内在线考试。
- 服务端状态是考试时间、提交和阅卷的最终事实源。
- 考试生命周期中的关键动作必须可审计。

## 已确认技术栈

技术栈决策见 `docs/adr/0002-java-spring-boot-backend-stack.md`。

- 前端：Vue 3 + Vite + TypeScript + Element Plus
- 后端：Java 21 + Spring Boot 3.5.x + Maven
- 数据库：MySQL 8.x
- ORM：MyBatis-Plus
- 权限：Spring Security
- 缓存/锁/限流：Redis 后续接入

## 仍待决策事项

以下事项确定前需要写 ADR：

- 部署目标
- OpenAPI 生成方式
- Redis 幂等和限流实现方式
- MySQL migration 工具
- 生产可观测技术栈
- E2E 工具落地方式
