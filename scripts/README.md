# 脚本入口

这些脚本是开发者、CI 和 Agent 的稳定入口：

- `setup.ps1`
- `dev.ps1`
- `check.ps1`
- `test.ps1`
- `db-reset.ps1`
- `install-git-hooks.ps1`

当前仓库只包含 Harness Engineering 基线。技术栈确定后，需要把这些脚本接入真实的包管理器、服务、数据库和测试命令。
