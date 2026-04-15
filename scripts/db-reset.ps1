param(
    [switch]$Seed
)

$ErrorActionPreference = "Stop"

Write-Host "数据库重置入口"
Write-Host "当前尚未选择数据库。"
Write-Host "数据库确定后，在这里重建本地 schema 并加载 fixture。"

if ($Seed) {
    Write-Host "已请求加载 seed 数据。"
}

