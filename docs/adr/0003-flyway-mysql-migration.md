# ADR-0003：采用 Flyway 管理 MySQL Migration

## 状态

Accepted

## 背景

阶段 2 需要把考试作答 MVP 从内存仓储迁移到 MySQL，并继续使用 MyBatis-Plus。数据库结构变更必须通过 migration 管理，不能依赖手工在 Navicat 或 MySQL 控制台执行 SQL。

当前本地 MySQL 已可通过 `127.0.0.1:3306` 访问，开发库名为 `cxcode_exam`，本地默认账号为 `root`，密码为空。

## 决策

采用 Flyway 管理 MySQL migration：

- migration 文件放在 `apps/api/src/main/resources/db/migration/`。
- 文件命名使用 Flyway 标准，例如 `V1__create_exam_core_tables.sql`。
- Spring Boot `mysql` profile 启用 Flyway 自动迁移。
- 本地默认连接使用 `MYSQL_USERNAME`、`MYSQL_PASSWORD` 环境变量覆盖，默认值为 `root` 和空密码。
- MyBatis-Plus 只负责 entity / mapper / SQL 映射，不负责 schema migration。

阶段 2 的第一批表结构覆盖考试作答 MVP：

- 考试、考生分配、试卷、试卷题目
- 试题和选项
- 作答、答卷、答案项
- 提交幂等记录

## 影响

- 数据库结构变更进入 git，可被本地、CI 和部署环境重复执行。
- Navicat 可以继续用于查看数据和调试 SQL，但不能作为结构变更事实源。
- 需要维护 migration 的向前兼容性；破坏性变更必须单独评审并给出回滚方案。
- 数据库仓储行为属于强制 TDD 范围，必须先有仓储集成测试再实现。

## 已考虑的替代方案

- Liquibase：功能更强，但当前项目早期阶段不需要 XML/YAML changelog 和复杂回滚模型。
- 手工 SQL / Navicat：适合本地调试，不适合作为多人协作和部署事实源。
- Hibernate DDL Auto：会弱化 schema 可控性，不符合 Harness Engineering 对 migration 的要求。
