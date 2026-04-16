# 阶段 2 任务计划：MySQL 与 MyBatis-Plus

## 目标

将阶段 1 的考试作答 MVP 从内存仓储迁移到 MySQL 持久化，使用 Flyway 管理表结构，使用 MyBatis-Plus 实现仓储。

## 范围

- 设计并创建考试作答 MVP 所需 MySQL 表结构。
- 引入 Flyway migration。
- 实现 MyBatis-Plus entity / mapper。
- 实现 MySQL 版 `ExamStore`。
- 增加仓储集成测试和 API 回归验证。
- 更新数据库、测试、脚本、ADR、任务清单和 CHANGELOG 文档。

## 非范围

- 不实现正式登录、用户、角色、权限表。
- 不接入 Redis 幂等和限流。
- 不实现阅卷、成绩发布、监考数据。
- 不改动前端业务行为，除非 API 契约变化导致必须调整。

## 影响区域

- 领域：不改变领域模型和状态机。
- API：保持阶段 1 API 响应兼容。
- 数据：新增 MySQL schema、migration、seed 数据策略。
- UI：仅做 E2E 回归验证。
- 安全：仍使用阶段 1 演示 `x-user-id`，不扩大权限模型。
- 可观测：保持现有结构化审计日志。
- 测试：新增 MySQL 仓储集成测试，保留现有 API 测试和 E2E。

## Definition of Ready 检查

- [x] 目标清楚
- [x] 范围清楚
- [x] 领域行为清楚
- [x] API 变化清楚
- [x] 数据变化清楚
- [x] 权限影响清楚
- [x] 是否强制 TDD 已确认
- [x] TDD 失败测试已规划
- [x] 测试计划清楚
- [x] 风险已列出

## 实施计划

- [x] 编写 MySQL 仓储集成失败测试：考试列表、开始作答、自动保存、重复提交幂等。
- [x] 引入 Flyway 依赖和 MySQL profile 配置。
- [x] 创建 `V1__create_exam_core_tables.sql`。
- [x] 实现 MyBatis-Plus entity / mapper。
- [x] 实现 MySQL `ExamStore` 并按 profile 切换。
- [x] 更新 `scripts/db-reset.ps1` 支持提示或执行本地 MySQL 重置。
- [x] 运行 API 测试、E2E 和完整质量门禁。
- [x] 更新文档、任务清单和 CHANGELOG。

## 验收标准

- [x] `mysql` profile 下 Flyway 能创建核心表。
- [x] MySQL 仓储能列出考试、创建/恢复作答、保存答案、提交答卷。
- [x] 重复提交同一幂等键返回同一业务结果。
- [x] 默认 `memory` profile 仍可运行现有质量门禁。
- [x] Playwright E2E 仍通过。

## 验证计划

- [x] `mvn -s config/maven/settings.xml -f apps/api/pom.xml test`
- [x] MySQL 仓储集成测试
- [x] `scripts/check.ps1`
- [x] `scripts/e2e.ps1`

## 回滚计划

代码层面回滚本阶段提交即可恢复内存仓储默认行为。数据库层面本地开发库可通过 `scripts/db-reset.ps1` 删除并重建；生产环境破坏性回滚必须单独写 migration 和备份恢复方案。

## Definition of Done 检查

- [x] 行为已实现
- [x] 强制 TDD 范围内已完成 Red-Green-Refactor
- [x] 测试已新增或更新
- [x] 日志/审计已补齐
- [x] 文档已更新
- [x] `docs/task-list.md` 已更新
- [x] `CHANGELOG.md` 已更新
- [x] 质量门禁已通过
