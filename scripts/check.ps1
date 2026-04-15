param(
    [switch]$Ci
)

$ErrorActionPreference = "Stop"

Write-Host "质量门禁入口"

$requiredFiles = @(
    "AGENTS.md",
    "ARCHITECTURE.md",
    "CODING_GUIDE.md",
    "docs/README.md",
    "docs/context.md",
    "docs/domain-model.md",
    "docs/state-machines.md",
    "docs/api-contract.md",
    "docs/data-model.md",
    "docs/security.md",
    "docs/observability.md",
    "docs/testing.md",
    "docs/quality-gates.md",
    "docs/hooks.md",
    "docs/definition-of-ready.md",
    "docs/definition-of-done.md",
    "docs/feedback-loop.md",
    "docs/architecture-drift.md",
    "docs/agent-boundaries.md",
    "docs/non-functional-requirements.md",
    "docs/dependencies.md",
    "docs/project-scripts.md",
    "docs/runbook.md",
    "docs/tasks/TEMPLATE.md",
    "docs/adr/TEMPLATE.md",
    ".githooks/pre-commit",
    ".githooks/pre-commit.ps1"
)

$missingFiles = @()
foreach ($file in $requiredFiles) {
    if (-not (Test-Path $file)) {
        $missingFiles += $file
    }
}

if ($missingFiles.Count -gt 0) {
    Write-Host "Harness Engineering 基线文件缺失："
    foreach ($file in $missingFiles) {
        Write-Host " - $file"
    }
    exit 1
}

Write-Host "Harness Engineering 基线文件完整。"
Write-Host "当前尚未选择应用技术栈。"
Write-Host "技术栈确定后，此脚本必须继续接入：格式检查、lint、类型检查、测试、架构边界检查、API 契约校验、构建。"

if ($Ci) {
    Write-Host "已启用 CI 模式。"
}
