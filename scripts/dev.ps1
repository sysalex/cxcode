param(
    [switch]$Detached,
    [ValidateSet("memory", "mysql")]
    [string]$ApiProfile = "memory"
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

Write-Host "开发环境启动入口"
Write-Host "启动 Spring Boot API 和 Vue 前端。"
Write-Host "后端 profile：$ApiProfile"

$javaVersionOutput = cmd /c "java -version 2>&1"
if (-not ($javaVersionOutput -match 'version "21\.')) {
    Write-Host "当前 Java 不是 21，后端无法启动。请安装 JDK 21 并更新 JAVA_HOME。"
    Write-Host $javaVersionOutput
    exit 1
}

if ($Detached) {
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PWD'; mvn -s config/maven/settings.xml -f apps/api/pom.xml spring-boot:run -Dspring-boot.run.profiles=$ApiProfile"
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PWD'; pnpm --filter @cxcode/web dev"
    Write-Host "已用独立窗口启动后端和前端。"
    exit 0
}

Write-Host "请在另一个终端运行：pnpm --filter @cxcode/web dev"
Set-Location apps\api
Invoke-NativeCommand "mvn" @("-s", "..\..\config\maven\settings.xml", "spring-boot:run", "-Dspring-boot.run.profiles=$ApiProfile")
