# ADR-0002：采用 Java Spring Boot + Vue 3 技术栈

## 状态

Accepted

## 背景

在线考试系统后端需要承载考试规则、权限判断、提交幂等、审计、事务和后续数据库迁移。用户已明确要求后端使用 Java，并确认数据库、ORM、权限和前端方案。

Spring 官方文档显示，当前稳定版本线包括 Spring Boot 4.0.5、3.5.13、3.4.13、3.3.13；Spring Boot 4.0.5 至少需要 Java 17，Maven 3.6.3 或更高版本可用。为了降低第一阶段生态兼容风险，项目先采用 Spring Boot 3.5.x，目标 JDK 为 Java 21。

参考：

- https://docs.spring.io/spring-boot/index.html
- https://docs.spring.io/spring-boot/system-requirements.html

## 决策

第一阶段技术栈：

- 后端：Java 21 + Spring Boot 3.5.x + Maven
- 前端：Vue 3 + Vite + TypeScript + Element Plus
- 数据库：MySQL 8.x
- ORM：MyBatis-Plus
- 权限：Spring Security
- 缓存/锁/限流：Redis 后续接入
- 测试：JUnit 5 + Spring Boot Test；前端使用 Vitest；E2E 后续接 Playwright
- 可观测：Spring Boot Actuator + 结构化日志，后续接 OpenTelemetry

第一阶段实现策略：

- 后端代码按 Java 21 和 Spring Boot 3.5.x 编写。
- API、领域模型、应用服务先使用内存仓储打通作答闭环。
- MySQL、MyBatis-Plus mapper、migration 和 Redis 幂等/限流在下一阶段接入。
- Spring Security 先建立安全配置入口，MVP 暂用 `x-user-id` 演示用户，后续替换为真实认证。

后端采用分层结构：

```text
domain -> application -> infrastructure
                         -> api
                         -> security
```

核心业务规则放在 `domain`，用例编排放在 `application`，内存仓储和后续数据库实现放在 `infrastructure`，HTTP 接口放在 `api`，认证授权放在 `security`。

## 影响

- 后端领域模型可用 Java 类型系统表达，适合长期维护。
- Spring Boot 提供成熟的 Web、校验、测试、Actuator、事务和安全生态。
- MyBatis-Plus 适合后续接入 MySQL，并保留 SQL 可控性。
- 当前机器默认 Java 为 1.8，无法构建 Java 21/Spring Boot 3.5 项目；本地后端验证需要安装或切换到 JDK 21。
- 前后端不再同语言，API 契约后续必须通过 OpenAPI 或契约测试保持一致。

## 已考虑的替代方案

- TypeScript + NestJS：类型共享好，但用户明确要求后端 Java。
- TypeScript + Fastify：轻量，但不符合后端 Java 要求。
- Spring Boot 4.x：当前已稳定，但第一阶段采用 3.5.x 更稳妥，后续可通过 ADR 升级。
- PostgreSQL：事务和复杂数据能力强，但用户已明确选择 MySQL。
