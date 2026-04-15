param(
    [switch]$Detached
)

$ErrorActionPreference = "Stop"

Write-Host "开发环境启动入口"
Write-Host "启动 Spring Boot API 和 Vue 前端。"

$javaVersionOutput = cmd /c "java -version 2>&1"
if (-not ($javaVersionOutput -match 'version "21\.')) {
    Write-Host "当前 Java 不是 21，后端无法启动。请安装 JDK 21 并更新 JAVA_HOME。"
    Write-Host $javaVersionOutput
    exit 1
}

if ($Detached) {
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PWD\apps\api'; mvn spring-boot:run"
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PWD'; pnpm --filter @cxcode/web dev"
    Write-Host "已用独立窗口启动后端和前端。"
    exit 0
}

Write-Host "请在另一个终端运行：pnpm --filter @cxcode/web dev"
Set-Location apps\api
mvn spring-boot:run
