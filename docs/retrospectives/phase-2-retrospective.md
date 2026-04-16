# 阶段 2 回顾：MySQL 与 MyBatis-Plus

## 完成情况

- 计划任务：设计 MySQL 表结构、引入 Flyway、实现 MyBatis-Plus entity / mapper、实现 MySQL 仓储、增加仓储集成测试。
- 实际完成：阶段 2 的 2.1 到 2.5 已全部完成，默认 `memory` profile 保留，`mysql` profile 可通过环境变量连接本地 MySQL。
- 未完成：正式认证权限、Redis 幂等限流、生产可观测和部署流水线不属于阶段 2。

## 质量指标

- 后端测试：`ExamControllerTest`、`ExamDomainServiceTest`、`MysqlExamStoreIntegrationTest` 共 7 个测试通过。
- 前端测试：Vitest 当前无单元测试文件，使用 `--passWithNoTests` 通过。
- E2E 测试：Playwright 使用本机 Google Chrome，核心考试作答流程 1 个测试通过。
- 质量门禁：使用 JDK 21 运行 `scripts/check.ps1` 通过，`scripts/e2e.ps1` 通过。
- 新增技术债：默认 profile 仍为 `memory`；后续联调、部署或 CI 需要显式启用 `mysql` profile 并提供 MySQL 服务。

## 问题与处理

- 问题：MyBatis mapper 扫描最初放在应用主类上，导致默认 `memory` profile 启动测试时加载数据库 mapper。
- 处理：将 mapper 扫描移动到 `@Profile("mysql")` 配置类，保证内存模式和 MySQL 模式边界清晰。
- 问题：MySQL `TIMESTAMP(6)` 会截断 Java `Instant` 纳秒精度，导致幂等提交结果回读时间不一致。
- 处理：应用服务统一把服务端时间截断到微秒，匹配 MySQL 持久化精度。
- 问题：PowerShell 中文脚本在无 BOM 时会被 Windows PowerShell 按本地编码解析。
- 处理：变更过的脚本保存为 UTF-8 BOM，并用脚本语法解析和安全提示入口验证。

## 下阶段注意事项

- 阶段 3 接入 Spring Security 后，必须移除演示用 `x-user-id` 认证方式。
- 权限模型要覆盖考生、教师、管理员、监考员，并补充越权访问测试。
- MySQL 集成测试依赖本地 `cxcode_exam` 数据库，后续 CI 需要配置 MySQL 服务或独立测试库。
- 提交幂等在当前阶段覆盖重复提交；并发提交、超时提交和 Redis 幂等锁应在可靠性阶段继续强化。
