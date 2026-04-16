# 阶段 1 回顾：考试作答 MVP

## 完成情况

- 计划任务：建立 Spring Boot 后端骨架、Vue 3 前端骨架、考试作答领域规则、内存仓储、核心 API、考生作答页面和质量门禁。
- 实际完成：阶段 1 的 1.1 到 1.7 已全部完成，并已提交到 `main`。
- 未完成：MySQL 持久化、正式认证权限和 Redis 幂等限流不属于阶段 1，已登记到后续阶段和技术债。

## 质量指标

- 后端测试：`ExamControllerTest` 和 `ExamDomainServiceTest` 共 6 个测试通过。
- 前端测试：Vitest 当前无单元测试文件，使用 `--passWithNoTests` 通过。
- E2E 测试：Playwright 使用本机 Google Chrome，核心考试作答流程 1 个测试通过。
- 质量门禁：使用 JDK 21 运行 `scripts/check.ps1` 通过。
- 新增技术债：继续保留 `TD-001` 内存仓储、`TD-002` 演示用户头、`TD-003` Element Plus 首包较大。

## 问题与处理

- 问题：阶段 1 完成后未同步新增阶段回顾文档。
- 处理：补充本回顾文档，并将阶段 1 回顾纳入 `scripts/check.ps1` 基线文件检查。
- 问题：Playwright 浏览器安装命令在本机环境中下载耗时且被中断。
- 处理：改为使用本机 Google Chrome，并通过 `scripts/e2e.ps1` 清理代理环境变量后运行。
- 问题：Vitest 曾误收集 Playwright 测试文件。
- 处理：Playwright 测试统一使用 `*.e2e.ts` 命名，并在配置中显式设置 `testMatch`。

## 下阶段注意事项

- 阶段 2 开始前必须先明确 MySQL migration 工具，并新增 ADR。
- 数据库仓储替换内存仓储时属于强制 TDD 范围，需要先写仓储集成测试或契约测试。
- 考试提交、自动保存和答卷锁定在数据库阶段必须继续保持幂等性和服务端时间准则。
- Spring Security 正式接入前，不能把 `x-user-id` 演示头当作生产认证方案。
