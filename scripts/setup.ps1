param(
    [switch]$Ci
)

$ErrorActionPreference = "Stop"

Write-Host "初始化入口"
Write-Host "安装前端依赖，并检查 Java/Maven 环境。"

pnpm install

$javaVersionOutput = cmd /c "java -version 2>&1"
Write-Host $javaVersionOutput
mvn -s config/maven/settings.xml -version

if ($Ci) {
    Write-Host "已启用 CI 模式。"
}
