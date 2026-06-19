# 🎮 GAMEVILA - 现代化游戏平台

**在线访问：** http://120.79.160.66:80  
**管理员账号：** `admin` / `123456`

> 从 HTML+JS 到 Vue 3 的全栈技术演进实践

[![Vue](https://img.shields.io/badge/Vue-3.x-4FC08D?logo=vue.js)](https://vuejs.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=springboot)](https://spring.io/projects/spring-boot)
[![Redis](https://img.shields.io/badge/Redis-Session%20Store-DC382D?logo=redis)](https://redis.io/)
[![GitHub](https://img.shields.io/badge/GitHub-Open%20Source-181717?logo=github)](https://github.com/ll88-tt/game-platform-react-node)

---

## 📖 项目简介

GAMEVILA 是一个采用**前后端分离架构**的现代化游戏平台，覆盖游戏浏览、智能搜索、分类筛选、用户注册登录、VIP 订阅支付、收藏与浏览历史、管理员后台等完整业务链路。

**本项目最大的亮点在于完整记录了从前端到后端的技术演进过程**：从最初的原生 HTML/JS 快速原型，到 Vue 3 + Vite 工程化重构，再到 Git 版本控制与规范化协作——这是一次对现代前端工程体系和全栈开发能力的系统性实践。

---

## ✨ 核心功能

### 用户端

| 功能模块 | 说明 |
| :--- | :--- |
| 🔍 **智能搜索** | Trie 树 + N-Gram 算法，支持模糊匹配、搜索建议、热门搜索与搜索历史 |
| 🎯 **分类浏览** | 按冒险 / RPG / 竞速等分类筛选，VIP 专属游戏权限控制 |
| 👤 **用户系统** | 注册、登录、Session 持久化（Redis），全站用户头像菜单 |
| 👑 **VIP 订阅** | 月度 / 季度 / 年度 / 终身会员，模拟支付下单与订单确认 |
| 📋 **订单记录** | 查看历史订单、待支付订单继续支付、VIP 到期时间展示 |
| ❤️ **游戏收藏** | 登录用户收藏存数据库，免费/VIP 用户均可收藏最多 100 款 |
| 📜 **浏览历史** | 登录用户云端同步浏览记录；未登录时使用 localStorage，登录后自动合并 |
| 🔔 **VIP 提醒** | 到期前提醒 Banner、登录后到期提示 |
| 💬 **意见反馈** | 用户提交反馈，管理员后台查看处理 |

### 用户中心（`/user-center`）

- 修改密码
- 订单记录（含继续支付）
- 我的收藏（VIP 游戏锁定提示）
- 浏览记录（云端同步、删除、清空）
- VIP 状态卡片（套餐、到期时间、剩余天数）

### 管理后台

| 页面 | 路径 | 功能 |
| :--- | :--- | :--- |
| 📊 **数据看板** | `/admin-dashboard` | 用户数、VIP 数、游戏数、订单统计 |
| 🎮 **游戏管理** | `/admin-games` | 游戏 CRUD、上下架、VIP/免费切换、封面上传 |
| 💎 **套餐管理** | 管理后台内嵌 | 会员方案价格、推荐标签、启用/禁用 |
| 📥 **反馈管理** | `/admin-feedback` | 查看与处理用户反馈 |

### 后端能力

| 能力 | 技术实现 |
| :--- | :--- |
| 🔐 **权限管理** | Spring Security + Session 过滤器，普通用户 / 管理员分级 |
| 🗄️ **会话管理** | Redis 分布式 Session，服务无状态化 |
| ⏰ **VIP 自动降级** | 定时任务每小时扫描到期 VIP 并降级，登录/查订阅时即时校验 |
| 📦 **个人库容量** | 收藏与浏览历史上限按 FREE / VIP / ADMIN 分级控制 |
| 🔒 **密码安全** | BCrypt 加密存储 |

---

## 👑 VIP 与容量规则

| 用户类型 | 收藏上限 | 浏览历史上限 |
| :--- | :---: | :---: |
| 免费用户 | 100 | 20 |
| VIP 会员 | 100 | 100 |
| 管理员 | 500 | 500 |

- VIP 到期后自动降级为普通用户（终身 VIP 与管理员不受影响）
- VIP 专属游戏对无权限用户展示锁定状态，引导开通会员

---

## 🧭 技术演进历程

### 第一阶段：传统 HTML + JavaScript（项目初期）

**前端**：HTML5 / CSS3 / 原生 JavaScript (ES6+) / Font Awesome  
**后端**：Spring Boot 3.x / Spring Security / Spring Data JPA / MySQL 8.0 / Redis / BCrypt

```
✅ 快速原型开发，适合学习阶段
✅ 简单直接，无需额外构建工具
❌ 代码复用性差，大量重复 HTML/CSS/JS
❌ 状态管理困难，依赖 localStorage 和全局变量
❌ 路由需手动处理，不支持 SPA
❌ 组件化程度低，维护成本高
```

### 第二阶段：Vue 3 + Vite 现代化重构（当前阶段）

**前端升级**：Vue 3 (Composition API) / Vite 5.x / Vue Router 4 / Pinia / Axios / Scoped CSS  
**后端升级**：RESTful API 完善、订阅支付、管理后台、用户中心、定时任务

```
✅ 组件化开发：UserAvatarMenu、VipExpiryBanner 等公共组件
✅ 响应式数据：Pinia 统一管理登录态与 VIP 状态
✅ 路由守卫：登录页 / 管理员页权限拦截
✅ 开发体验：Vite 热更新 + API 代理
✅ 业务完善：收藏、浏览历史、订单、VIP 到期降级等全链路
```

> **关键设计决策**：将高频访问的用户登录态从 MySQL 剥离，迁移至 Redis 存储；收藏与浏览历史写入 MySQL，与 VIP 体系绑定，实现跨设备同步。

### 第三阶段：版本控制与开源协作（进行中）

```
✅ Git 版本控制：feat 分支（Vue-verson / perfect-verson）
✅ GitHub 托管：ll88-tt/game-platform-react-node
📝 规范化提交：Conventional Commits（规划中）
🏷️ 版本标签：v1.0.0、v2.0.0 语义化版本（规划中）
🤖 CI/CD：GitHub Actions 自动化测试与部署（规划中）
```

---

## 🛠️ 完整技术栈

| 层级 | 技术 | 用途 |
| :--- | :--- | :--- |
| **前端** | Vue 3 / Vite / Vue Router / Pinia / Axios | SPA 构建、路由、状态管理、HTTP 请求 |
| **后端** | Spring Boot 3.x / Spring Security / JPA / Hibernate | RESTful API、认证鉴权、ORM |
| **数据库** | MySQL 8.0 / Redis | 持久化存储 / 分布式 Session |
| **安全** | BCrypt / Spring Session | 密码加密 / 会话管理 |
| **定时任务** | `@Scheduled` | VIP 到期自动降级 |
| **开发工具** | Maven / npm / JDK 17 / Node.js 20.x / Git | 依赖管理、构建、版本控制 |

---

## 📂 项目结构

```
D:\FW\Game\
├── frontend/                         # Vue 3 前端项目
│   ├── src/
│   │   ├── views/                    # 页面组件
│   │   │   ├── HomeView.vue          # 首页（搜索、分类、收藏、浏览记录）
│   │   │   ├── AboutView.vue         # 关于 / 技术演进介绍
│   │   │   ├── ServiceView.vue       # VIP 订阅与支付
│   │   │   ├── SubscriptionView.vue  # 订阅状态与订单
│   │   │   ├── UserCenterView.vue    # 用户中心
│   │   │   ├── ContactView.vue       # 联系与反馈
│   │   │   ├── AdminDashboardView.vue
│   │   │   ├── AdminGamesView.vue
│   │   │   └── AdminFeedbackView.vue
│   │   ├── components/               # 公共组件
│   │   │   ├── UserAvatarMenu.vue
│   │   │   └── VipExpiryBanner.vue
│   │   ├── router/                   # 路由与守卫
│   │   ├── stores/                   # Pinia 状态
│   │   ├── api/                      # API 封装
│   │   └── utils/                    # 搜索历史、浏览历史、VIP 提醒
│   ├── vite.config.js                # 开发端口 3000，代理 /api → 8080
│   └── package.json
│
├── src/main/java/game/demo/          # Spring Boot 后端
│   ├── config/                       # Security、Web、Session 过滤器
│   ├── controller/                   # REST 控制器
│   ├── entity/                       # 实体（User、Game、Order、GameFavorite…）
│   ├── repository/                   # JPA 仓库
│   ├── service/                      # 业务逻辑
│   ├── scheduler/                    # VIP 到期定时任务
│   └── dto/                          # 数据传输对象
│
├── src/main/resources/
│   ├── application.properties
│   └── static/                       # 生产构建输出目录
│
├── pom.xml
└── README.md
```

---

## 🔌 主要 API 概览

| 模块 | 路径前缀 | 说明 |
| :--- | :--- | :--- |
| 认证 | `/api/login` `/api/register` `/api/check-login` | 登录注册与会话检查 |
| 游戏 | `/api/games` | 列表、搜索、分类、访问权限校验 |
| 订阅 | `/api/subscription` `/api/orders` | VIP 状态与订单列表 |
| 支付 | `/api/payment` | 创建订单、确认支付 |
| 用户 | `/api/user` | 密码、收藏、浏览历史、库容量 |
| 管理 | `/api/admin` | 游戏、套餐、看板、反馈 |
| 反馈 | `/api/feedback` | 用户提交反馈 |

---

## 🚀 快速开始

### 环境要求

- JDK 17+
- Node.js 20.x+
- MySQL 8.0+
- Redis
- Maven 3.x+

### 1. 克隆项目

```bash
git clone git@github.com:ll88-tt/game-platform-react-node.git
cd game-platform-react-node
```

### 2. 配置数据库

编辑 `src/main/resources/application.properties`：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/Game?...
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

### 3. 启动 Redis 与 MySQL

```bash
# Windows 示例
net start MySQL95
redis-server
```

### 4. 启动后端

```bash
mvn spring-boot:run
```

后端默认：`http://localhost:8080`

### 5. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端默认：`http://localhost:3000`（Vite 已配置 `/api` 代理至 8080）

### 6. 生产构建（可选）

```bash
cd frontend
npm run build
```

构建产物输出至 `src/main/resources/static/`，可由 Spring Boot 统一托管。

---

## 🗺️ 页面路由

| 路径 | 页面 | 权限 |
| :--- | :--- | :--- |
| `/` `/home` | 首页 | 公开 |
| `/about` | 关于 | 公开 |
| `/service` | VIP 订阅 | 公开 |
| `/contact` | 联系反馈 | 公开 |
| `/subscription` | 我的订阅 | 需登录 |
| `/user-center` | 用户中心 | 需登录 |
| `/admin-dashboard` | 管理看板 | 管理员 |
| `/admin-games` | 游戏管理 | 管理员 |
| `/admin-feedback` | 反馈管理 | 管理员 |

---

## 🔮 后续规划

- [ ] GitHub Actions CI/CD 自动化流水线
- [ ] 单元测试与集成测试覆盖
- [ ] Docker 容器化一键部署
- [ ] 浏览记录 / 搜索历史全量服务端化
- [ ] 真实支付网关对接（微信 / 支付宝）
- [ ] 前端路由懒加载与性能优化

---

## 👤 关于我

广州大学 软件工程 本科在读（2023-2027）

本项目为个人全栈技术实践，旨在系统性地掌握前后端分离架构、现代前端工程化以及分布式系统的基础设计思路。欢迎通过 [Issue](https://github.com/ll88-tt/game-platform-react-node/issues) 交流或提出建议。
