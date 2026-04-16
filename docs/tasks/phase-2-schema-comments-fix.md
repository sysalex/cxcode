# 阶段 2 修复任务：MySQL Schema 注释

## 目标

补齐阶段 2 MySQL 核心表和字段注释，保证数据库结构可以直接在 Navicat、`information_schema` 和运维排障中读懂。

## 范围

- 新增 Flyway migration，补充核心表 table comment。
- 补充核心表所有字段 column comment。
- 增加 MySQL 集成测试，验证表和字段注释不为空。
- 更新数据模型文档、任务清单和 CHANGELOG。

## 非范围

- 不修改已执行并已提交的 `V1__create_exam_core_tables.sql`。
- 不新增业务表。
- 不改变仓储行为、API 行为或前端行为。

## Definition of Ready 检查

- [x] 目标明确
- [x] 范围明确
- [x] 非范围明确
- [x] 影响模块明确
- [x] 领域行为明确：不改变领域行为
- [x] 状态流转明确：不改变状态流转
- [x] API 变化明确：无 API 变化
- [x] 数据变化明确：仅新增 schema 注释
- [x] 权限影响明确：无权限影响
- [x] 可观测影响明确：提升数据库可读性
- [x] 测试计划明确
- [x] 是否属于 TDD 强制范围已经明确：生产缺陷修复，强制 TDD
- [x] 预期先写的失败测试已经明确：`information_schema` 表和字段注释检查
- [x] 验收标准明确
- [x] 风险和回滚需求已列出

## 实施计划

- [x] 编写 MySQL schema 注释失败测试。
- [x] 新增 `V2__add_exam_core_table_comments.sql`。
- [x] 验证 MySQL 集成测试通过。
- [x] 运行完整质量门禁。
- [x] 更新文档、任务清单和 CHANGELOG。

## 验收标准

- [x] 阶段 2 核心表都有 table comment。
- [x] 阶段 2 核心表所有字段都有 column comment。
- [x] Flyway 可从 V1 迁移到 V2。
- [x] MySQL 集成测试通过。
- [x] `scripts/check.ps1` 通过。

## 回滚计划

本次 migration 只添加元数据注释，不改变数据和业务约束。需要回滚时可新增反向 migration 清空注释；不修改已执行 migration 历史。
