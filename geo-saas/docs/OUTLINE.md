# GEO SaaS 监测平台 — 产品与技术大纲

> 版本：v0.1 · 技术栈：Vue3 + Java(Spring Boot) · 目标：完整监测 / 多 AI 验证 / 套餐支付闭环

---

## 1. 产品定位

面向品牌与代理商的 **生成式引擎优化（GEO）可见度监测 SaaS**：

- **客户端**：品牌配置 Prompt、选择 AI 引擎、查看提及率 / 引用 / 竞品 SOV、购买套餐
- **管理后台**：套餐与配额、租户与订单、引擎适配器、任务调度与风控
- **核心价值**：固定问题集 × 定时多引擎采样 × 指标看板；**不内置黑帽投喂**

---

## 2. 用户角色与端

| 角色 | 端 | 能力 |
|------|----|------|
| 租户管理员 | 客户端 | 品牌、Prompt、监测、报表、成员、账单 |
| 租户成员 | 客户端 | 查看与有限编辑（按权限） |
| 平台超管 | 管理后台 | 套餐、租户、支付、引擎、系统配置 |
| 运营 | 管理后台 | 订单、工单、配额调整、公告 |

---

## 3. 功能模块大纲

### 3.1 账号与租户

- 注册 / 登录 / 找回密码（JWT）
- 租户（Workspace）隔离：所有业务数据带 `tenant_id`
- 成员邀请、角色（Owner / Admin / Analyst / Viewer）

### 3.2 品牌与竞品

- 品牌主档：名称、别名、域名、行业、地区 / 语言
- 竞品集：手工添加或根据品类建议
- 实体一致性：别名用于提及识别，降低漏检 / 误检

### 3.3 Prompt（提示词）管理 — 核心

- Prompt 分组（品类意图 / 对比 / 推荐 / 功效等）
- 字段：原文、语言、国家、标签、冻结版本号
- **版本化**：编辑产生新 version；历史采样绑定旧 version，保证曲线可比
- 导入：CSV / 从 SEO 关键词转 Prompt（规则模板）
- 配额：套餐限制「活跃 Prompt 数」

### 3.4 多 AI 平台验证（引擎执行）

支持引擎（可插拔 Adapter）：

| 引擎 Code | 名称 | 采集方式（MVP） |
|-----------|------|-----------------|
| chatgpt | ChatGPT | HTTP Adapter（可接官方 API / 代理） |
| perplexity | Perplexity | HTTP Adapter |
| gemini | Gemini | HTTP Adapter |
| deepseek | DeepSeek | HTTP Adapter |
| doubao | 豆包 | HTTP Adapter |
| kimi | Kimi | HTTP Adapter |
| qwen | 通义千问 | HTTP Adapter |
| yuanbao | 腾讯元宝 | HTTP Adapter（Mock 优先） |

执行链路：

```
调度器 → 拉取到期任务 → 按 (prompt, engine, locale) 扇出
  → Adapter.ask(prompt, options)
  → 保存原始回答 ResponseRaw
  → 解析器：提及 / 位次 / 情感 / 引用 URL
  → 写入 ResponseMetric + Citation
  → 聚合日/周趋势 VisibilitySnapshot
```

关键设计：

- **MockAdapter**：无真实 Key 时返回可复现假数据，保证本地联调
- **RealAdapter**：读取 `engine_credential` 配置调用真实 API
- 同一 Prompt 可配置 **重复次数**（降随机噪声）与 **采样频率**（日 / 周）
- 失败重试、超时、限流、按套餐扣减「月响应额度」

### 3.5 监测看板与报表

- 总览：提及率、推荐位次、SOV、情感分布
- 按引擎拆分、按 Prompt 拆分、趋势图
- 原文回溯：每次采样保留完整回答
- 引用来源：域名 TopN，指导投放
- 竞品对比热力图
- 导出 CSV / PDF（二期）

### 3.6 套餐、配额、支付 — 完整商业链路

**套餐维度（可配置）：**

- 活跃品牌数、活跃 Prompt 数、引擎数、月响应额度、成员席位、历史保留天数、API 访问

**链路：**

```
浏览套餐 → 创建订单 → 选择支付渠道（微信/支付宝 Mock + 可扩展）
  → 支付回调验签 → 订单成功 → 开通/续费订阅
  → 写入租户配额 → 用量计量（Prompt 运行扣减）
  → 欠费 / 超额：只读或暂停调度
```

- 订单状态：待支付 / 已支付 / 已关闭 / 已退款
- 订阅：试用 / 月付 / 年付；到期提醒
- 发票与账单列表（客户端）
- 后台可手工调配额、补单、退款标记

### 3.7 管理后台

- 仪表盘：租户数、MRR 粗算、任务成功率、引擎健康
- 租户管理、套餐 CRUD、订单查询
- 引擎开关与凭证（密文存储）
- 系统公告、操作审计日志

### 3.8 系统与运维

- 配置中心：application.yml + DB 动态配置
- 任务调度：Spring `@Scheduled` + DB 任务表（可升 Quartz / XXL-JOB）
- 审计日志、登录日志
- Docker Compose：MySQL + Redis + 后端 + 双前端

---

## 4. 信息架构（页面）

### 客户端 `web-client`

1. 登录 / 注册  
2. 工作台 Dashboard  
3. 品牌管理  
4. Prompt 库（含版本）  
5. 监测任务 / 立即验证  
6. 可见度报表（总览 / 引擎 / 引用 / 竞品）  
7. 套餐与支付  
8. 账单与订阅  
9. 团队成员  
10. 账号设置  

### 管理后台 `web-admin`

1. 登录  
2. 运营总览  
3. 租户列表  
4. 套餐管理  
5. 订单 / 支付流水  
6. AI 引擎配置  
7. 任务监控与重跑  
8. 系统配置 / 审计  

---

## 5. 技术架构

```
┌─────────────┐  ┌─────────────┐
│ web-client  │  │  web-admin  │   Vue3 + Vite + Element Plus + ECharts
└──────┬──────┘  └──────┬──────┘
       │ REST/JWT       │
┌──────▼────────────────▼──────┐
│     Spring Boot 3 API        │
│  Auth · Tenant · Prompt ·    │
│  Engine · Job · Billing      │
└──────┬───────────┬───────────┘
       │           │
   ┌───▼───┐   ┌───▼───┐
   │ MySQL │   │ Redis │  （会话/限流/任务锁）
   └───────┘   └───────┘
```

| 层 | 选型 |
|----|------|
| 后端 | Java 17、Spring Boot 3.2、Spring Security、MyBatis-Plus、Flyway |
| 前端 | Vue 3、Vite、Vue Router、Pinia、Element Plus、Axios、ECharts |
| DB | MySQL 8 |
| 缓存 | Redis |
| 支付 | 抽象 `PaymentGateway`；MVP 提供 Mock；预留微信/支付宝接口 |
| AI | `AiEngineAdapter` 接口 + Mock + 可配置 HTTP |

---

## 6. 核心数据模型

```
tenant                 租户
user_account          用户账号
tenant_member          租户成员与角色
brand                  品牌
brand_alias            品牌别名
competitor             竞品关联
prompt_group           Prompt 分组
prompt                 Prompt（活跃）
prompt_version         Prompt 版本快照
engine_provider        AI 引擎定义
tenant_engine          租户已开通引擎
monitor_job            监测任务（绑定品牌/频率）
monitor_job_prompt     任务-Prompt 关联
run_batch              一次调度批次
run_item               单次 (prompt_version × engine) 执行
response_raw           原始回答
response_metric        解析指标（提及/位次/情感）
citation               引用来源 URL/域名
visibility_daily       日聚合快照
plan                   套餐定义
plan_quota             套餐配额项
subscription           租户订阅
usage_counter          用量计数（月）
order_info             订单
payment_record         支付流水
audit_log              审计
```

---

## 7. 关键 API 清单（MVP）

### 认证
- `POST /api/auth/register` `POST /api/auth/login` `GET /api/auth/me`

### 品牌 / Prompt
- `CRUD /api/brands`
- `CRUD /api/prompts` `POST /api/prompts/{id}/versions`

### 监测
- `POST /api/jobs` 创建监测任务  
- `POST /api/jobs/{id}/run` 立即验证（全引擎或指定引擎）  
- `GET /api/reports/overview` `GET /api/reports/trends`  
- `GET /api/runs/{id}` 查看原文与指标  

### 套餐支付
- `GET /api/plans`  
- `POST /api/orders` `POST /api/orders/{id}/pay`  
- `POST /api/payments/callback/{channel}`  
- `GET /api/subscriptions/current` `GET /api/orders`

### 管理端
- ` /api/admin/tenants` ` /api/admin/plans` ` /api/admin/orders`  
- ` /api/admin/engines` ` /api/admin/jobs`

---

## 8. 实施分期

| 阶段 | 交付 | 状态 |
|------|------|------|
| P0 大纲与仓库骨架 | 本文档 + monorepo 目录 | 进行中 |
| P1 后端领域与 API | 认证、租户、品牌、Prompt、Mock 引擎、任务、套餐支付 | 下一步 |
| P2 双前端联调 | 客户端 + 管理后台关键页可跑通 | 下一步 |
| P3 Docker 一键起 | compose + 初始化 SQL | 下一步 |
| P4 真实引擎 / 真支付 | 配置 Key 后可切生产 Adapter | 后续 |

---

## 9. 非目标（本期不做）

- 黑帽批量发文 /「保排名」能力  
- Agent 爬虫浏览器农场（真机 UI 采集可二期）  
- 复杂 RaaS 效果付费合同引擎  
- 多语言 i18n 全量（先中文）  

---

## 10. 目录约定

```
geo-saas/
  docs/OUTLINE.md          # 本大纲
  backend/                 # Spring Boot
  web-client/              # 客户端 Vue3
  web-admin/               # 管理后台 Vue3
  sql/                     # 初始化与示例数据
  scripts/                 # 启动脚本
  docker-compose.yml
  README.md
```
