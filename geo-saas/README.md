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

## 本机运行（推荐）

Cloud Agent 里的 `localhost` 在你电脑浏览器里打不开时，把代码拉到本机即可。

### 环境要求

- JDK 17+（建议 17/21）
- Maven 3.8+
- Node.js 18+ / npm

### 拉取代码

```bash
# 已有 pearProject 仓库
git fetch origin
git checkout cursor/geo-saas-platform-1331

# 或只关心 GEO 工程时，用 IDE 打开子目录
# File → Open → .../pearProject/geo-saas
cd geo-saas
```

### 一键启动（推荐，任意目录可执行）

```bash
bash geo-saas/scripts/start-local.sh
# 或
bash /绝对路径/geo-saas/scripts/start-local.sh
```

脚本会检测端口：已在跑则跳过，避免 `Port 8080 was already in use`。

### 或开 3 个终端手动启动

**注意：不要把下面三行粘到同一个已进入 `backend` 的终端里连跑。**
`cd geo-saas/backend && mvn ...` 成功/失败后，当前目录会留在 `backend`，
接着再 `cd geo-saas/web-client` 会报 `No such file or directory`。

```bash
# 终端 1 — 若 8080 已有服务可跳过
cd /你的路径/geo-saas/backend && mvn spring-boot:run

# 终端 2
cd /你的路径/geo-saas/web-client && npm install && npm run dev

# 终端 3
cd /你的路径/geo-saas/web-admin && npm install && npm run dev
```

释放端口（需要重启时）：

```bash
fuser -k 8080/tcp 5173/tcp 5174/tcp
```

本机浏览器直接打开：

- 客户端 http://localhost:5173 （`demo` / `demo123`）
- 管理后台 http://localhost:5174 （`admin` / `admin123`）
- API http://localhost:8080
- H2 控制台 http://localhost:8080/h2-console  
  JDBC URL: `jdbc:h2:file:./data/geosaas`

> 默认用嵌入式 H2 文件库，**不需要先装 MySQL/Redis**。数据在 `backend/data/`。

### 单独做成「本地项目」

`geo-saas/` 已自包含（backend + 双前端 + 文档）。你可以：

1. 用 Cursor/IDE **只打开 `geo-saas` 目录** 当独立项目；或  
2. 以后把该目录拆到新仓库：`git subtree split` / 复制目录后 `git init`。

功能上无需再改代码，本机与云端是同一套。

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
