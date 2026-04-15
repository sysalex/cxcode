param(
    [string]$Filter = ""
)

$ErrorActionPreference = "Stop"

Write-Host "测试入口"
Write-Host "当前尚未选择应用技术栈。"
Write-Host "后续测试范围：单元测试、集成测试、E2E、安全测试、性能冒烟测试。"

if ($Filter -ne "") {
    Write-Host "测试过滤条件：$Filter"
}

