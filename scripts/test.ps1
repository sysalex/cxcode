param(
    [string]$Filter = ""
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

Write-Host "测试入口"
Write-Host "运行前端测试和后端测试。"

if ($Filter -ne "") {
    Write-Host "测试过滤条件：$Filter"
}

Invoke-NativeCommand "pnpm" @("--filter", "@cxcode/web", "test")

if ($Filter -eq "e2e") {
    .\scripts\e2e.ps1
}

$javaVersionOutput = cmd /c "java -version 2>&1"
if (-not ($javaVersionOutput -match 'version "21\.')) {
    Write-Host "当前 Java 不是 21，跳过后端测试。请安装 JDK 21 后运行 mvn -f apps/api/pom.xml test。"
    Write-Host $javaVersionOutput
    exit 0
}

Invoke-NativeCommand "mvn" @("-s", "config/maven/settings.xml", "-f", "apps/api/pom.xml", "test")
