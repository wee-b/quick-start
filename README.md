# Quick-Start

基于 Spring Cloud Gateway 的微服务脚手架项目，使用 Nacos 服务发现，JWT + Redis 统一认证，Knife4j 网关聚合 API 文档。

## 模块架构

```
                        qs-common
                    (公共 POJO + JWT + 安全工具)
                   /        |         \
                  /         |          \
           qs-gateway    qs-client    qs-draw
           :8080          :8085         :8086
              │              │             │
              │ /client/draw/**           │
              ├───────────────────────────►
              │ /client/user/**            │
              ├─────────────►              │
              │ /admin/**                  │
              ├─────────────►              │
              │              │             │
              │ http://:8080/doc.html  ← 聚合 API 文档
              │  ├── 用户与系统 (qs-client)
              │  └── 抽奖 (qs-draw)
```

| 模块 | 端口 | 说明 |
|---|---|---|
| **qs-common** | — | 公共模块：实体类、DTO/VO、JWT 工具、TokenStoreService、异常处理、自定义注解 |
| **qs-gateway** | 8080 | Spring Cloud Gateway 网关：JWT 验签 + Redis token 校验，转发用户信息至下游 |
| **qs-client** | 8085 | 用户与系统服务：登录注册、用户管理、角色菜单权限 |
| **qs-draw** | 8086 | 抽奖服务：抽奖活动管理、抽奖码生成、MQ 异步抽奖 |

### 请求流程

```

网关 AuthGlobalFilter:
  无 token → 透传（不注入 X-User-Code）
  有 token → 验签 → 注入 X-User-Code → 透传
  token 无效 → 401

下游 GatewayAuthInterceptor:
  @NoNeedLogin → 放行
  无注解 + 无 X-User-Code → 401
  无注解 + 有 X-User-Code → 查权限 → 放行


客户端 → Gateway（JWT 验签 + Redis 校验 token 有效性）
              │
              ├─ 放行 X-User-Code / X-Token-Id header
              ↓
         qs-client / qs-draw
              │
              ├─ GatewayAuthInterceptor 读取 header 设置 SecurityContext
              ↓
         Controller → Service → Mapper
```

### 认证机制

- **网关层**：`AuthGlobalFilter` 校验 JWT 签名 + Redis 中 token 有效性，白名单放行登录/注册/Knife4j 文档等路径
- **服务层**：拦截器从 `X-User-Code` / `X-Token-Id` header 中读取用户信息注入 `SecurityContext`，不重复验签

## 技术栈与版本

| 依赖 | 版本 | 说明 |
|---|---|---|
| JDK | 17 | |
| Spring Boot | 3.5.4 | |
| Spring Cloud | 2023.0.3 | |
| Spring Cloud Alibaba | 2023.0.1.0 | Nacos 服务发现 |
| Nacos | 2.x | 服务注册与发现、配置管理 |
| MyBatis-Plus | 3.5.16 | ORM + 分页 |
| MyBatis-Spring | 4.0.0 | MyBatis-Spring 集成（覆盖 MyBatis-Plus 传递的旧版本） |
| MySQL Connector | 8.0.33 | 数据库驱动 |
| P6Spy | 3.9.1 | SQL 执行监控 |
| jjwt | 0.12.6 | JWT 签发与验签 |
| Spring Security Crypto | 6.5.1 | 密码加密 |
| Redisson | 3.44.0 | 分布式锁（仅 qs-client / qs-draw） |
| Knife4j | 4.6.0 | API 文档 + 网关聚合 |
| Fastjson | 2.0.57 | JSON 序列化 |
| Hutool | 5.8.23 | 工具类 |
| RabbitMQ | 3.13 | 抽奖消息异步处理 |
| Druid | 1.2.25 | 数据库连接池 |

## 本地运行

### 环境依赖

- JDK 17+
- MySQL 8.0+
- Redis
- Nacos（默认 `localhost:8848`，账号 `nacos/nacos`）
- RabbitMQ（qs-draw 需要）

### RabbitMQ 部署

```bash
docker pull rabbitmq:3.13-management
docker run -d --name rabbitmq \
  -p 5672:5672 -p 15672:15672 \
  -e RABBITMQ_DEFAULT_USER=admin \
  -e RABBITMQ_DEFAULT_PASS=123456 \
  rabbitmq:management
```

### 配置说明

公共配置在 `qs-common/src/main/resources/dev/base.yaml`，通过 `${qs.*}` 占位符引用外部化配置（Nacos 或环境变量）：

- `qs.datasource.*` — 数据库连接
- `qs.redis.*` — Redis 连接
- `qs.rabbitmq.*` — RabbitMQ 连接
- `qs.jwt.secret` — JWT 签名密钥

## 潜在问题

1. qs-draw 对 User 表只有只读查询，不会修改表结构
2. 暂不涉及跨服务写事务，无需 Seata
3. RabbitMQ 配置在 qs-draw 本地，与 qs-client 共享同一个 MQ 实例
