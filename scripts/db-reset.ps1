param(
    [switch]$ConfirmReset,
    [switch]$Seed,
    [string]$Database = "cxcode_exam",
    [string]$Username = $(if ($env:MYSQL_USERNAME) { $env:MYSQL_USERNAME } else { "root" }),
    [string]$Password = $(if ($env:MYSQL_PASSWORD) { $env:MYSQL_PASSWORD } else { "" })
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

Write-Host "数据库重置入口"
Write-Host "目标数据库：$Database"

if (-not $ConfirmReset) {
    Write-Host "未执行重置。该操作会清空目标数据库 schema。"
    Write-Host "确认要重置本地开发库时，请运行：.\scripts\db-reset.ps1 -ConfirmReset"
    exit 0
}

$jdbcUrl = "jdbc:mysql://localhost:3306/$Database"

Invoke-NativeCommand "mvn" @(
    "-s",
    "config/maven/settings.xml",
    "-f",
    "apps/api/pom.xml",
    "-Dflyway.url=$jdbcUrl",
    "-Dflyway.user=$Username",
    "-Dflyway.password=$Password",
    "-Dflyway.cleanDisabled=false",
    "flyway:clean",
    "flyway:migrate"
)

if ($Seed) {
    Write-Host "seed 数据会在后端使用 mysql profile 启动时自动写入空库。"
}
