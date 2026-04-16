# 依赖与合规治理

## 依赖规则

- 通过包管理器 lockfile 锁定依赖版本。
- 新增运行时依赖前必须评估必要性。
- 安全、认证、校验、可观测、测试基础设施优先使用成熟库。
- 不为简单逻辑引入重依赖。

## 当前关键运行时依赖

- Spring Boot：后端 Web、配置、测试、Actuator 和安全基础。
- MyBatis-Plus：MySQL entity / mapper 和仓储映射。
- MySQL Connector/J：连接 MySQL 8.x。
- Flyway：管理数据库 migration。

## 漏洞治理

CI 后续必须检查：

- 已知漏洞
- 废弃包
- license 兼容性
- lockfile 完整性

安全关键漏洞必须有负责人和修复时间。

## License 规则

生产前必须：

- 定义允许的 license。
- 记录例外。
- 在 CI 中加入 license 扫描。

## 升级规则

依赖升级必须包含：

- breaking change 检查
- 受影响流程测试
- 高风险运行时依赖的回滚计划
