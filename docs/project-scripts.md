# 项目脚本入口

脚本是开发者、CI 和 Agent 的稳定执行入口。

## 入口脚本

- `scripts/setup.ps1`：安装依赖并准备本地环境。
- `scripts/dev.ps1`：启动开发环境。
- `scripts/check.ps1`：运行本地质量门禁。
- `scripts/test.ps1`：运行测试。
- `scripts/db-reset.ps1`：重置本地数据库并加载测试数据。
- `scripts/install-git-hooks.ps1`：安装本地 git hooks。

## 规则

- 有脚本时，不要猜命令。
- 新增 package manager 命令时，必须挂到这些脚本后面。
- 本地质量门禁和 CI 应尽量复用同一套脚本。
- 新增服务时，更新 `scripts/dev.ps1`。
- 新增数据库或队列时，更新 `scripts/setup.ps1` 和 `scripts/db-reset.ps1`。
- 脚本变化时，更新本文档。

## 计划中的命令映射

最终映射取决于技术栈。典型 Web 项目可按以下方式落地：

```text
setup      -> 安装依赖、复制 env 模板、启动本地基础服务
dev        -> 启动 API、Web、数据库和 worker
check      -> 格式检查、lint、类型检查、测试、构建
test       -> 单元测试、集成测试、E2E 测试
db-reset   -> 重建 schema 并加载 fixture
hooks      -> 安装 pre-commit / pre-push 自动检查
```
