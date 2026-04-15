param(
    [switch]$Seed
)

$ErrorActionPreference = "Stop"

Write-Host "数据库重置入口"
Write-Host "数据库已确定为 MySQL。当前 MVP 仍使用内存仓储，MySQL schema 将在数据库阶段接入。"

if ($Seed) {
    Write-Host "已请求加载 seed 数据。"
}
