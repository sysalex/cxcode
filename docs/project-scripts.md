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

当前脚本映射：

```text
setup      -> 安装前端依赖，并检查 Java/Maven 环境
dev        -> 启动 Spring Boot API 和 Vue 前端
check      -> 格式检查、lint、前端类型检查、前端测试、前端构建、后端测试
test       -> 前端测试；JDK 21 可用时运行后端测试
db-reset   -> 当前提示 MySQL 阶段待接入
hooks      -> 安装 pre-commit / pre-push 自动检查
```

后端 Maven 命令使用项目内 `config/maven/settings.xml`，避免依赖用户全局 Maven 配置。
