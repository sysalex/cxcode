param(
    [switch]$Detached
)

$ErrorActionPreference = "Stop"

Write-Host "开发环境启动入口"
Write-Host "当前尚未选择应用技术栈。"
Write-Host "技术栈确定后，在这里启动 Web、API、数据库和 worker。"

if ($Detached) {
    Write-Host "已请求后台模式。"
}

