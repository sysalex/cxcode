# Hooks 自动化

Hooks 用于在提交前提供最快反馈。

## 必要 hooks

- pre-commit：运行不依赖外部服务的快速检查。
- pre-push：技术栈确定后运行更完整的本地检查。

## 当前基线行为

当前仓库还没有业务代码，pre-commit 先运行 `scripts/check.ps1`，用于检查 Harness Engineering 基线文件是否完整。

技术栈确定后，pre-commit 应包括：

- 格式检查
- 变更文件 lint
- 可行范围内的类型检查
- 架构边界检查
- 可行范围内的受影响单元测试

pre-push 应包括：

- 完整本地质量门禁
- API 契约校验
- 集成测试冒烟

## 规则

- hooks 必须调用仓库脚本，不要重复写逻辑。
- hooks 失败应修复原因，不应绕过。
- 紧急绕过必须补后续任务。

