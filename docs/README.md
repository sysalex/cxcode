# Harness Engineering 文档索引

本目录是在线考试系统的工程控制中心，包含上下文、架构约束、自动化入口、验证机制、运行可观测和反馈循环。

## 核心文档

- `../AGENTS.md`：Agent 工作入口和任务循环
- `../ARCHITECTURE.md`：架构规则和依赖边界
- `../CODING_GUIDE.md`：基础编码规范
- `context.md`：项目上下文和事实源说明
- `glossary.md`：业务术语表
- `product-specs/online-exam-system.md`：产品范围和核心流程
- `domain-model.md`：领域对象、聚合、事件和业务不变量
- `state-machines.md`：考试、作答、答卷、阅卷、成绩状态机
- `api-contract.md`：API 格式、错误、幂等和版本规则
- `data-model.md`：数据模型、迁移、保留和恢复规则
- `security.md`：认证、权限、威胁模型和审计要求
- `observability.md`：日志、指标、链路追踪、告警和运行诊断
- `testing.md`：测试分层、测试数据和关键场景
- `tdd.md`：测试驱动开发规范
- `quality-gates.md`：本地和 CI 质量门禁
- `hooks.md`：本地 hooks 自动化
- `definition-of-ready.md`：任务开始标准
- `definition-of-done.md`：任务完成标准
- `feedback-loop.md`：修复、验证和持续改进循环
- `architecture-drift.md`：架构漂移检测
- `agent-boundaries.md`：Agent 行为边界
- `non-functional-requirements.md`：非功能需求
- `dependencies.md`：依赖和合规治理
- `project-scripts.md`：标准脚本入口
- `runbook.md`：发布、回滚和故障处理手册
- `task-list.md`：全局任务计划清单
- `retrospectives/`：阶段回顾

## 模板

- `tasks/TEMPLATE.md`：任务计划模板
- `adr/TEMPLATE.md`：架构决策记录模板

## 更新规则

如果代码改动影响了业务行为、安全策略、API 契约、数据结构或运行方式，必须在同一次变更中更新对应文档。
