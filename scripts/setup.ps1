param(
    [switch]$Ci
)

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

Write-Host "初始化入口"
Write-Host "安装前端依赖，并检查 Java/Maven 环境。"

Invoke-NativeCommand "pnpm" @("install")

$javaVersionOutput = cmd /c "java -version 2>&1"
Write-Host $javaVersionOutput
Invoke-NativeCommand "mvn" @("-s", "config/maven/settings.xml", "-version")

if ($Ci) {
    Write-Host "已启用 CI 模式。"
}
