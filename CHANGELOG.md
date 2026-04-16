# Changelog

本文件记录项目的重要变更。所有任务完成后必须更新本文件。

## Unreleased

### Added

- 补齐阶段 2 MySQL 核心表和字段注释，并增加 schema 注释回归测试。
- 记录阶段 2 MySQL migration 决策，采用 Flyway 管理数据库结构变更。
- 接入 Flyway、MyBatis-Plus MySQL 仓储和 MySQL 仓储集成测试。
- `dev.ps1` 支持选择后端 profile，`db-reset.ps1` 支持确认后重建本地 MySQL schema。
- 补齐阶段 2 MySQL 与 MyBatis-Plus 回顾，并纳入 Harness 基线检查。
- 增加 TDD 测试驱动开发规范。
- 增加全局任务计划清单。
- 增加阶段回顾目录。
- 将 TDD、任务清单和 CHANGELOG 接入 Agent 工作流、DoD 和反馈循环。
- 接入 Playwright，使用本机 Chrome 运行考试作答 E2E 测试。
- 强化 PowerShell 脚本退出码检查，避免质量门禁和 E2E 失败被吞掉。
- 补齐阶段 1 考试作答 MVP 回顾，并纳入 Harness 基线检查。

## 2026-04-15

### Added

- 建立 Harness Engineering 规范基线。
- 确认技术栈：Java 21、Spring Boot 3.5.x、MySQL、MyBatis-Plus、Spring Security、Vue 3。
- 建立 Spring Boot 后端 MVP 骨架。
- 建立 Vue 3 前端考试作答页面。
- 增加项目内 Maven settings，并跑通完整质量门禁。
