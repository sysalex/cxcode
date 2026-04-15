param(
    [switch]$Ci
)

$ErrorActionPreference = "Stop"

Write-Host "初始化入口"
Write-Host "当前尚未选择应用技术栈。"
Write-Host "技术栈确定后，在这里安装依赖、准备 env 文件，并启动需要的本地基础服务。"

if ($Ci) {
    Write-Host "已启用 CI 模式。"
}

