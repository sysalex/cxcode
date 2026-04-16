$ErrorActionPreference = "Stop"

function Invoke-NativeCommand {
    param(
        [string]$Command,
        [string[]]$Arguments = @()
    )

    & $Command @Arguments
    if ($LASTEXITCODE -ne 0) {
        throw "命令失败，退出码 $LASTEXITCODE：$Command $($Arguments -join ' ')"
    }
}

Write-Host "E2E 测试入口"
Write-Host "使用本机 Google Chrome，不下载 Playwright 浏览器。"

$env:all_proxy = ""
$env:ALL_PROXY = ""
$env:http_proxy = ""
$env:HTTP_PROXY = ""
$env:https_proxy = ""
$env:HTTPS_PROXY = ""

Invoke-NativeCommand "pnpm" @("--filter", "@cxcode/web", "test:e2e")
