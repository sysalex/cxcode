$ErrorActionPreference = "Stop"

if (-not (Test-Path ".git")) {
    Write-Host "当前目录不是 git 仓库。初始化 git 后再安装 hooks。"
    exit 0
}

git config core.hooksPath .githooks
Write-Host "Git hooks 已安装，来源目录：.githooks"

