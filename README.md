
# 🎮 GAMEVILA - 现代化游戏平台
在线访问：http://120.79.160.66:80

> 从 HTML+JS 到 Vue 3 的全栈技术演进实践

[![Vue](https://img.shields.io/badge/Vue-3.x-4FC08D?logo=vue.js)](https://vuejs.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=springboot)](https://spring.io/projects/spring-boot)
[![Redis](https://img.shields.io/badge/Redis-Session%20Store-DC382D?logo=redis)](https://redis.io/)
[![GitHub](https://img.shields.io/badge/GitHub-Open%20Source-181717?logo=github)](https://github.com/)

---

## 📖 项目简介

GAMEVILA 是一个采用前后端分离架构的现代化游戏平台，提供游戏浏览、智能搜索、分类筛选、用户注册登录、VIP 订阅系统、管理员权限管理等完整功能。

**本项目最大的亮点在于完整记录了从前端到后端的技术演进过程**：从最初的原生 HTML/JS 快速原型，到 Vue 3 + Vite 工程化重构，再到 Git 版本控制与规范化协作——这是一次对现代前端工程体系和全栈开发能力的系统性实践。


## ✨ 核心功能

| 功能模块 | 技术实现 | 说明 |
| :--- | :--- | :--- |
| 🔍 **智能搜索** | Trie 树 + N-Gram 算法 | 支持模糊匹配与实时搜索建议 |
| 👑 **VIP 系统** | 多层级会员模型 | 支持月度/季度/年度/终身订阅 |
| 🔐 **权限管理** | Spring Security 角色控制 | 普通用户与管理员分级权限 |
| 🗄️ **会话管理** | Redis 分布式 Session | 服务无状态化，支持横向扩展 |
| 📱 **响应式界面** | Vue 3 + Scoped CSS | 适配 PC 与移动端 |

## 🧭 技术演进历程

### 第一阶段：传统 HTML + JavaScript (项目初期)

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

### 第二阶段：Vue 3 + Vite 现代化重构 (当前阶段)

**前端升级**：Vue 3 (Composition API) / Vite 5.x / Vue Router 4 / Pinia / Axios / Scoped CSS  
**后端保持不变**

```
✅ 组件化开发：高复用性，易于维护
✅ 响应式数据：自动追踪依赖，视图自动更新
✅ 状态管理：Pinia 统一管理应用状态
✅ 路由系统：Vue Router 支持 SPA 和路由守卫
✅ 开发体验：Vite 热更新，秒级启动
✅ 生态完善：丰富的插件和组件库
```

> **关键设计决策**：将高频访问的用户登录态从 MySQL 剥离，迁移至 Redis 存储，降低数据库压力并提升认证接口响应速度。

### 第三阶段：版本控制与开源协作 (计划中)

```
📦 Git 版本控制：提交历史、分支管理、标签发布
🌐 GitHub 托管：代码开源、Issue 跟踪、PR 协作
📝 规范化提交：feat/fix/docs/style/refactor 等 Conventional Commits
🏷️ 版本标签：v1.0.0、v1.1.0、v2.0.0 语义化版本
🤖 CI/CD：GitHub Actions 自动化测试与部署
```

---

## 🛠️ 完整技术栈

| 层级 | 技术 | 用途 |
| :--- | :--- | :--- |
| **前端** | Vue 3 / Vite / Vue Router / Pinia / Axios | SPA 构建、路由、状态管理、HTTP 请求 |
| **后端** | Spring Boot 3.x / Spring Security / JPA / Hibernate | RESTful API、认证鉴权、ORM |
| **数据库** | MySQL 8.0 / Redis | 持久化存储 / 分布式 Session |
| **安全** | BCrypt | 密码加密 |
| **开发工具** | Maven / npm / JDK 17 / Node.js 20.x / Git | 依赖管理、构建、版本控制 |

---

## 📂 项目结构

```
D:\FW\Game\
├── frontend/                    # Vue 3 前端项目
│   ├── src/
│   │   ├── views/              # 页面组件
│   │   │   ├── HomeView.vue
│   │   │   ├── AboutView.vue
│   │   │   ├── ServiceView.vue
│   │   │   ├── ContactView.vue
│   │   │   └── AdminFeedbackView.vue
│   │   ├── components/         # 公共组件
│   │   ├── router/             # 路由配置
│   │   ├── stores/             # Pinia 状态管理
│   │   ├── api/                # API 接口封装
│   │   ├── App.vue             # 根组件
│   │   └── main.js             # 入口文件
│   ├── package.json
│   ├── vite.config.js
│   └── index.html
│
├── src/main/java/game/demo/    # Spring Boot 后端
│   ├── config/                 # 配置类
│   ├── controller/             # 控制器
│   ├── entity/                 # 实体类
│   ├── repository/             # 数据仓库
│   ├── service/                # 业务逻辑
│   └── GameApplication.java    # 启动类
│
├── src/main/resources/
│   ├── static/                 # 静态资源（旧 HTML 保留）
│   └── application.properties # 配置文件
│
├── pom.xml                     # Maven 配置
└── README.md                   # 本文件
```

---

## 🚀 快速开始

### 环境要求
- JDK 17+
- Node.js 20.x+
- MySQL 8.0+
- Redis
- Maven 3.x+

### 后端启动
```bash
# 克隆项目
git clone https://github.com/YOUR_USERNAME/gamevila.git
cd gamevila

# 配置数据库（修改 src/main/resources/application.properties）
# 启动后端
mvn spring-boot:run
```

### 前端启动
```bash
cd frontend
npm install
npm run dev
```

前端服务默认运行在 `http://localhost:5173`，后端 API 默认运行在 `http://localhost:8080`。

---

## 🔮 后续规划

- [ ] 接入 GitHub Actions 实现 CI/CD 自动化流水线
- [ ] 补充单元测试与集成测试
- [ ] Docker 容器化部署
- [ ] 前端性能优化与懒加载
- [ ] 搜索模块 N-Gram 分词精度提升

---

## 👤 关于我

广州大学 软件工程 本科在读（2023-2027）

本项目为个人全栈技术实践，旨在系统性地掌握前后端分离架构、现代前端工程化以及分布式系统的基础设计思路。欢迎通过 Issue 交流或提出建议。
```
