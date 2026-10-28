# GEO SaaS 监测平台

Vue3 客户端 + Vue3 管理后台 + Java Spring Boot 后端。

覆盖：**多 AI 平台 Prompt 验证、可见度监测报表、套餐配额、Mock 支付闭环**。

详细大纲见 [`docs/OUTLINE.md`](docs/OUTLINE.md)。

## 目录

```
geo-saas/
  docs/OUTLINE.md
  backend/          Spring Boot 3 + JPA + H2/JWT
  web-client/       租户客户端 :5173
  web-admin/        管理后台   :5174
  scripts/
```

## 快速启动

### 1. 后端

```bash
cd backend
mvn spring-boot:run
```

- API: http://localhost:8080
- H2 控制台: http://localhost:8080/h2-console  
  JDBC URL: `jdbc:h2:file:./data/geosaas`

### 2. 客户端

```bash
cd web-client && npm install && npm run dev
```

打开 http://localhost:5173  
演示账号：`demo` / `demo123`

### 3. 管理后台

```bash
cd web-admin && npm install && npm run dev
```

打开 http://localhost:5174  
管理员：`admin` / `admin123`

## 演示账号

| 端 | 账号 | 密码 |
|----|------|------|
| 客户端 | demo | demo123 |
| 管理后台 | admin | admin123 |

## 完整业务链路（建议演示顺序）

1. 客户端登录 → 品牌管理新建品牌与竞品  
2. Prompt 库录入买家问题（编辑会升版本）  
3. **即时验证**：勾选多引擎执行 Mock 采样  
4. 查看批次原文、提及率、引用域名  
5. 创建监测任务并「立即运行」  
6. 报表页查看提及率趋势  
7. 套餐页 Mock 支付升级 → 订阅与用量变化  
8. 管理后台查看租户 / 订单 / 引擎  

## 配置说明

`backend/src/main/resources/application.yml`

- `geosaas.engine.mode=mock|real`：real 时走 OpenAI 兼容 HTTP（需引擎配置 endpoint + apiKey）
- `geosaas.payment.mode=mock`：本地秒支付；可扩展微信/支付宝回调 `/api/payments/callback/{channel}`

## 技术栈

- 后端：Java 17、Spring Boot 3.2、Spring Security JWT、JPA、H2（可切 MySQL）
- 前端：Vue 3、Vite、Element Plus、ECharts、Pinia、Axios
