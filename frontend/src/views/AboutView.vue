<template>
  <div class="about-container">
    <!-- 导航栏 -->
    <header class="header">
      <div class="logo">
        <i class="fas fa-gamepad" style="margin-right: 6px; color: #c084fc;"></i>
        GAMEVILA
      </div>

      <nav class="nav-links">
        <router-link to="/home">首页</router-link>
        <router-link to="/about" class="active">关于</router-link>
        <router-link to="/service">服务</router-link>
        <router-link to="/contact">联系</router-link>

        <router-link
            v-if="userStore.isAdmin"
            to="/admin-feedback"
            class="admin-btn"
        >
          <i class="fas fa-inbox"></i> 查看反馈 <i class="fas fa-arrow-right"></i>
        </router-link>

        <router-link to="/service" class="buy-now">
          订阅VIP <i class="fas fa-arrow-right"></i>
        </router-link>
      </nav>

      <div class="avatar-wrapper" @click="toggleDropdown">
        <button class="avatar-btn">
          <i
              class="fas fa-user-circle avatar-icon"
              :style="{ color: userStore.isLoggedIn ? '#c084fc' : '#94a3b8' }"
          ></i>
          <span class="avatar-text">
            {{ userStore.isLoggedIn ? userStore.currentUser : '游客' }}
            <i v-if="userStore.isLoggedIn" v-html="permissionIcon"></i>
          </span>
          <i class="fas fa-chevron-down" style="font-size: 0.8rem;"></i>
        </button>

        <div v-show="showDropdown" class="dropdown-menu">
          <div v-if="userStore.isLoggedIn" class="user-info">
            <i class="fas fa-user-check"></i> 欢迎，{{ userStore.currentUser }}
            <br/>
            <small style="color: #a78bfa;">
              {{ permissionName }}
              <span v-if="userStore.vipExpiryTime && userStore.vipExpiryTime !== '永久'">
                (至 {{ userStore.vipExpiryTime }})
              </span>
            </small>
          </div>

          <button v-if="userStore.isAdmin" class="dropdown-item" @click="goToAdminPanel">
            <i class="fas fa-shield-alt"></i> 管理后台
          </button>

          <button v-if="userStore.isLoggedIn" class="dropdown-item" @click="goToService">
            <i class="fas fa-crown"></i> 我的订阅
          </button>

          <button v-if="userStore.isLoggedIn" class="dropdown-item" @click="handleLogout">
            <i class="fas fa-sign-out-alt"></i> 退出账号
          </button>

          <button v-else class="dropdown-item" @click="openLoginModal">
            <i class="fas fa-sign-in-alt"></i> 登录
          </button>

          <div class="divider"></div>

          <button class="dropdown-item" @click="openHistoryPanel">
            <i class="fas fa-history"></i> 查看浏览记录
          </button>
        </div>
      </div>
    </header>

    <!-- 页面标题 -->
    <section class="page-hero">
      <h1 class="page-title">
        <i class="fas fa-info-circle"></i> 关于 GAMEVILA
      </h1>
      <p class="page-subtitle">从 HTML+JS 到 Vue 3 的技术演进之路</p>
    </section>

    <!-- 项目简介 -->
    <section class="content-section">
      <div class="section-header">
        <h2><i class="fas fa-project-diagram"></i> 项目简介</h2>
      </div>

      <div class="content-card">
        <p>
          GAMEVILA 是一个现代化的游戏平台，提供游戏浏览、搜索、分类筛选等功能。
          平台采用前后端分离架构，支持用户注册登录、VIP订阅系统、管理员权限管理等完整功能。
        </p>

        <div class="feature-grid">
          <div class="feature-item">
            <i class="fas fa-search"></i>
            <h3>智能搜索</h3>
            <p>基于 Trie 树和 N-Gram 算法的智能搜索引擎</p>
          </div>

          <div class="feature-item">
            <i class="fas fa-crown"></i>
            <h3>VIP 系统</h3>
            <p>多层级会员体系，支持月度/季度/年度/终身订阅</p>
          </div>

          <div class="feature-item">
            <i class="fas fa-shield-alt"></i>
            <h3>权限管理</h3>
            <p>完善的用户权限控制和管理员后台</p>
          </div>

          <div class="feature-item">
            <i class="fas fa-database"></i>
            <h3>会话管理</h3>
            <p>基于 Redis 的分布式 Session 管理</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 技术演进历程 -->
    <section class="content-section">
      <div class="section-header">
        <h2><i class="fas fa-code-branch"></i> 技术演进历程</h2>
      </div>

      <div class="timeline">
        <!-- 第一阶段：HTML + JS -->
        <div class="timeline-item">
          <div class="timeline-marker phase-1">
            <i class="fas fa-flag"></i>
          </div>
          <div class="timeline-content">
            <div class="timeline-header">
              <h3>第一阶段：传统 HTML + JavaScript</h3>
              <span class="timeline-date">项目初期</span>
            </div>

            <div class="tech-stack">
              <div class="tech-category">
                <h4>前端技术</h4>
                <div class="tech-tags">
                  <span class="tech-tag">HTML5</span>
                  <span class="tech-tag">CSS3</span>
                  <span class="tech-tag">原生 JavaScript (ES6+)</span>
                  <span class="tech-tag">Font Awesome</span>
                  <span class="tech-tag">Google Fonts</span>
                </div>
              </div>

              <div class="tech-category">
                <h4>后端技术</h4>
                <div class="tech-tags">
                  <span class="tech-tag">Spring Boot 3.x</span>
                  <span class="tech-tag">Spring Security</span>
                  <span class="tech-tag">Spring Data JPA</span>
                  <span class="tech-tag">MySQL 8.0</span>
                  <span class="tech-tag">Redis</span>
                  <span class="tech-tag">BCrypt</span>
                </div>
              </div>
            </div>

            <div class="phase-description">
              <p><strong>特点与局限：</strong></p>
              <ul>
                <li>✅ 快速原型开发，适合学习阶段</li>
                <li>✅ 简单直接，无需额外构建工具</li>
                <li>❌ 代码复用性差，大量重复 HTML/CSS/JS</li>
                <li>❌ 状态管理困难，依赖 localStorage 和全局变量</li>
                <li>❌ 路由需要手动处理，不支持 SPA</li>
                <li>❌ 组件化程度低，维护成本高</li>
              </ul>
            </div>
          </div>
        </div>

        <!-- 第二阶段：Vue 3 -->
        <div class="timeline-item">
          <div class="timeline-marker phase-2">
            <i class="fas fa-rocket"></i>
          </div>
          <div class="timeline-content">
            <div class="timeline-header">
              <h3>第二阶段：Vue 3 + Vite 现代化重构</h3>
              <span class="timeline-date">当前阶段</span>
            </div>

            <div class="tech-stack">
              <div class="tech-category">
                <h4>前端技术栈升级</h4>
                <div class="tech-tags">
                  <span class="tech-tag vue">Vue 3 (Composition API)</span>
                  <span class="tech-tag vite">Vite 5.x</span>
                  <span class="tech-tag">Vue Router 4</span>
                  <span class="tech-tag">Pinia</span>
                  <span class="tech-tag">Axios</span>
                  <span class="tech-tag">Scoped CSS</span>
                </div>
              </div>

              <div class="tech-category">
                <h4>后端保持不变</h4>
                <div class="tech-tags">
                  <span class="tech-tag">Spring Boot 3.x</span>
                  <span class="tech-tag">RESTful API</span>
                  <span class="tech-tag">自定义过滤器</span>
                </div>
              </div>
            </div>

            <div class="phase-description">
              <p><strong>核心优势：</strong></p>
              <ul>
                <li>✅ <strong>组件化开发</strong>：高复用性，易于维护</li>
                <li>✅ <strong>响应式数据</strong>：自动追踪依赖，视图自动更新</li>
                <li>✅ <strong>状态管理</strong>：Pinia 统一管理应用状态</li>
                <li>✅ <strong>路由系统</strong>：Vue Router 支持 SPA 和路由守卫</li>
                <li>✅ <strong>开发体验</strong>：Vite 热更新，秒级启动</li>
                <li>✅ <strong>生态完善</strong>：丰富的插件和组件库</li>
              </ul>
            </div>
          </div>
        </div>

        <!-- 第三阶段：Git + GitHub -->
        <div class="timeline-item">
          <div class="timeline-marker phase-3">
            <i class="fas fa-cloud-upload-alt"></i>
          </div>
          <div class="timeline-content">
            <div class="timeline-header">
              <h3>第三阶段：版本控制与开源协作</h3>
              <span class="timeline-date">即将实施</span>
            </div>

            <div class="tech-stack">
              <div class="tech-category">
                <h4>版本控制工具</h4>
                <div class="tech-tags">
                  <span class="tech-tag git">Git</span>
                  <span class="tech-tag github">GitHub</span>
                  <span class="tech-tag">Semantic Versioning</span>
                  <span class="tech-tag">Conventional Commits</span>
                </div>
              </div>

              <div class="tech-category">
                <h4>CI/CD（可选扩展）</h4>
                <div class="tech-tags">
                  <span class="tech-tag">GitHub Actions</span>
                  <span class="tech-tag">Automated Testing</span>
                  <span class="tech-tag">Auto Deploy</span>
                </div>
              </div>
            </div>

            <div class="phase-description">
              <p><strong>计划内容：</strong></p>
              <ul>
                <li>📦 <strong>Git 版本控制</strong>：提交历史、分支管理、标签发布</li>
                <li>🌐 <strong>GitHub 托管</strong>：代码开源、Issue 跟踪、PR 协作</li>
                <li>📝 <strong>规范化提交</strong>：feat/fix/docs/style/refactor 等类型</li>
                <li>🏷️ <strong>版本标签</strong>：v1.0.0、v1.1.0、v2.0.0 等语义化版本</li>
                <li>🤖 <strong>自动化流程</strong>：自动测试、自动部署、Code Review</li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 完整技术栈 -->
    <section class="content-section">
      <div class="section-header">
        <h2><i class="fas fa-layer-group"></i> 完整技术栈</h2>
      </div>

      <div class="tech-grid">
        <div class="tech-card">
          <div class="tech-card-header frontend">
            <i class="fas fa-laptop-code"></i>
            <h3>前端</h3>
          </div>
          <div class="tech-card-body">
            <div class="tech-list">
              <div class="tech-item">
                <span class="tech-name">Vue 3</span>
                <span class="tech-desc">渐进式 JavaScript 框架</span>
              </div>
              <div class="tech-item">
                <span class="tech-name">Vite</span>
                <span class="tech-desc">下一代前端构建工具</span>
              </div>
              <div class="tech-item">
                <span class="tech-name">Vue Router</span>
                <span class="tech-desc">官方路由管理器</span>
              </div>
              <div class="tech-item">
                <span class="tech-name">Pinia</span>
                <span class="tech-desc">Vue 状态管理库</span>
              </div>
              <div class="tech-item">
                <span class="tech-name">Axios</span>
                <span class="tech-desc">HTTP 客户端</span>
              </div>
            </div>
          </div>
        </div>

        <div class="tech-card">
          <div class="tech-card-header backend">
            <i class="fas fa-server"></i>
            <h3>后端</h3>
          </div>
          <div class="tech-card-body">
            <div class="tech-list">
              <div class="tech-item">
                <span class="tech-name">Spring Boot 3.x</span>
                <span class="tech-desc">Java Web 框架</span>
              </div>
              <div class="tech-item">
                <span class="tech-name">Spring Security</span>
                <span class="tech-desc">安全认证</span>
              </div>
              <div class="tech-item">
                <span class="tech-name">Spring Data JPA</span>
                <span class="tech-desc">数据持久层</span>
              </div>
              <div class="tech-item">
                <span class="tech-name">Hibernate</span>
                <span class="tech-desc">ORM 框架</span>
              </div>
              <div class="tech-item">
                <span class="tech-name">BCrypt</span>
                <span class="tech-desc">密码加密</span>
              </div>
            </div>
          </div>
        </div>

        <div class="tech-card">
          <div class="tech-card-header database">
            <i class="fas fa-database"></i>
            <h3>数据库</h3>
          </div>
          <div class="tech-card-body">
            <div class="tech-list">
              <div class="tech-item">
                <span class="tech-name">MySQL 8.0</span>
                <span class="tech-desc">关系型数据库</span>
              </div>
              <div class="tech-item">
                <span class="tech-name">Redis</span>
                <span class="tech-desc">Session 存储</span>
              </div>
              <div class="tech-item">
                <span class="tech-name">Spring Session</span>
                <span class="tech-desc">分布式会话</span>
              </div>
            </div>
          </div>
        </div>

        <div class="tech-card">
          <div class="tech-card-header tools">
            <i class="fas fa-tools"></i>
            <h3>开发工具</h3>
          </div>
          <div class="tech-card-body">
            <div class="tech-list">
              <div class="tech-item">
                <span class="tech-name">Maven</span>
                <span class="tech-desc">Java 构建工具</span>
              </div>
              <div class="tech-item">
                <span class="tech-name">npm</span>
                <span class="tech-desc">Node 包管理器</span>
              </div>
              <div class="tech-item">
                <span class="tech-name">JDK 17</span>
                <span class="tech-desc">Java 开发环境</span>
              </div>
              <div class="tech-item">
                <span class="tech-name">Node.js 20.x</span>
                <span class="tech-desc">JS 运行时</span>
              </div>
              <div class="tech-item">
                <span class="tech-name">Git</span>
                <span class="tech-desc">版本控制</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Git & GitHub 计划 -->
    <section class="content-section">
      <div class="section-header">
        <h2><i class="fab fa-github"></i> Git & GitHub 计划</h2>
      </div>

      <div class="github-plan">
        <div class="plan-step">
          <div class="step-number">1</div>
          <div class="step-content">
            <h3>初始化 Git 仓库</h3>
            <pre class="code-block"><code># 在项目根目录初始化 Git
cd D:\FW\Game
git init

# 创建 .gitignore 文件
# 忽略 node_modules、target、日志等</code></pre>
          </div>
        </div>

        <div class="plan-step">
          <div class="step-number">2</div>
          <div class="step-content">
            <h3>创建 GitHub 仓库</h3>
            <ul>
              <li>访问 https://github.com/new</li>
              <li>创建新仓库：gamevila</li>
              <li>选择 Public（公开）或 Private（私有）</li>
              <li>不要初始化 README（我们已有代码）</li>
            </ul>
          </div>
        </div>

        <div class="plan-step">
          <div class="step-number">3</div>
          <div class="step-content">
            <h3>关联远程仓库并推送</h3>
            <pre class="code-block"><code># 添加远程仓库
git remote add origin https://github.com/YOUR_USERNAME/gamevila.git

# 首次提交
git add .
git commit -m "feat: initial commit with Vue 3 migration"

# 推送到 GitHub
git branch -M main
git push -u origin main</code></pre>
          </div>
        </div>

        <div class="plan-step">
          <div class="step-number">4</div>
          <div class="step-content">
            <h3>规范化提交信息</h3>
            <pre class="code-block"><code># 功能开发
git commit -m "feat: add user authentication system"

# Bug 修复
git commit -m "fix: resolve login session timeout issue"

# 文档更新
git commit -m "docs: update README with installation guide"

# 代码重构
git commit -m "refactor: optimize game search algorithm"

# 样式调整
git commit -m "style: improve UI responsiveness"</code></pre>
          </div>
        </div>

        <div class="plan-step">
          <div class="step-number">5</div>
          <div class="step-content">
            <h3>版本标签与发布</h3>
            <pre class="code-block"><code># 创建版本标签
git tag -a v1.0.0 -m "Release version 1.0.0 - Vue 3 migration complete"

# 推送标签
git push origin v1.0.0

# 在 GitHub 上创建 Release
# 访问 https://github.com/YOUR_USERNAME/gamevila/releases
# 点击 "Create a new release"</code></pre>
          </div>
        </div>
      </div>
    </section>

    <!-- 项目结构 -->
    <section class="content-section">
      <div class="section-header">
        <h2><i class="fas fa-folder-tree"></i> 项目结构</h2>
      </div>

      <div class="project-structure">
        <pre class="structure-tree"><code>D:\FW\Game\
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
│   └── application.properties  # 配置文件
│
├── pom.xml                     # Maven 配置
└── README.md                   # 项目说明（待创建）</code></pre>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()

const showDropdown = ref(false)

const permissionIcon = computed(() => {
  switch(userStore.permissionLevel) {
    case 'LIFETIME': return '<i class="fas fa-crown" style="color: #f472b6; font-size: 0.8rem;"></i>'
    case 'YEARLY': return '<i class="fas fa-gem" style="color: #a78bfa; font-size: 0.8rem;"></i>'
    case 'QUARTERLY': return '<i class="fas fa-star" style="color: #fbbf24; font-size: 0.8rem;"></i>'
    case 'MONTHLY': return '<i class="fas fa-check-circle" style="color: #34d399; font-size: 0.8rem;"></i>'
    default: return '<i class="fas fa-user" style="color: #94a3b8; font-size: 0.8rem;"></i>'
  }
})

const permissionName = computed(() => {
  switch(userStore.permissionLevel) {
    case 'LIFETIME': return '终身会员'
    case 'YEARLY': return '年度会员'
    case 'QUARTERLY': return '季度会员'
    case 'MONTHLY': return '月度会员'
    default: return '免费用户'
  }
})

onMounted(async () => {
  await userStore.checkLoginStatus()
})

function toggleDropdown() {
  showDropdown.value = !showDropdown.value
}

function openLoginModal() {
  showDropdown.value = false
  alert('登录功能请在首页使用')
  router.push('/home')
}

async function handleLogout() {
  await userStore.logout()
  showDropdown.value = false
  alert('已退出登录')
}

function goToAdminPanel() {
  showDropdown.value = false
  router.push('/admin-feedback')
}

function goToService() {
  showDropdown.value = false
  router.push('/service')
}

function openHistoryPanel() {
  showDropdown.value = false
  alert('浏览记录功能请在首页查看')
}
</script>

<style scoped>
.about-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  margin-bottom: 32px;
}

.logo {
  font-size: 1.8rem;
  font-weight: 700;
  background: linear-gradient(135deg, #a78bfa, #f472b6);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.nav-links {
  display: flex;
  gap: 24px;
  align-items: center;
}

.nav-links a {
  color: #cbd5e1;
  text-decoration: none;
  transition: color 0.2s;
}

.nav-links a:hover,
.nav-links a.active {
  color: #a78bfa;
}

.admin-btn {
  background: linear-gradient(145deg, #ef4444, #f97316);
  padding: 8px 18px;
  border-radius: 40px;
  color: white !important;
  font-weight: 600;
  box-shadow: 0 6px 14px rgba(239, 68, 68, 0.3);
}

.buy-now {
  background: linear-gradient(145deg, #7c3aed, #a78bfa);
  padding: 8px 18px;
  border-radius: 40px;
  color: white !important;
  font-weight: 600;
  box-shadow: 0 6px 14px rgba(124, 58, 237, 0.3);
}

.avatar-wrapper {
  position: relative;
}

.avatar-btn {
  background: #161e2a;
  border: 1px solid #2a3748;
  padding: 8px 16px;
  border-radius: 8px;
  color: #edf2f7;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 8px;
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 12px;
  min-width: 200px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.5);
  z-index: 1000;
}

.user-info {
  padding: 16px;
  border-bottom: 1px solid #2a3748;
}

.dropdown-item {
  width: 100%;
  padding: 12px 16px;
  background: none;
  border: none;
  color: #cbd5e1;
  text-align: left;
  cursor: pointer;
}

.dropdown-item:hover {
  background: #1e2a3a;
}

.divider {
  height: 1px;
  background: #2a3748;
  margin: 8px 0;
}

.page-hero {
  text-align: center;
  padding: 60px 0;
  margin-bottom: 48px;
}

.page-title {
  font-size: 3rem;
  font-weight: 700;
  margin-bottom: 16px;
}

.page-subtitle {
  font-size: 1.2rem;
  color: #94a3b8;
}

.content-section {
  margin-bottom: 64px;
}

.section-header {
  margin-bottom: 32px;
}

.section-header h2 {
  font-size: 2rem;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 12px;
}

.content-card {
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 16px;
  padding: 32px;
}

.content-card p {
  color: #cbd5e1;
  line-height: 1.8;
  margin-bottom: 24px;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 24px;
  margin-top: 32px;
}

.feature-item {
  background: #0b0e14;
  padding: 24px;
  border-radius: 12px;
  text-align: center;
}

.feature-item i {
  font-size: 2.5rem;
  color: #a78bfa;
  margin-bottom: 16px;
}

.feature-item h3 {
  margin-bottom: 8px;
}

.feature-item p {
  color: #94a3b8;
  font-size: 0.9rem;
}

/* 时间线样式 */
.timeline {
  position: relative;
  padding-left: 40px;
}

.timeline::before {
  content: '';
  position: absolute;
  left: 15px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: linear-gradient(to bottom, #7c3aed, #a78bfa, #f472b6);
}

.timeline-item {
  position: relative;
  margin-bottom: 48px;
}

.timeline-marker {
  position: absolute;
  left: -40px;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 1rem;
}

.phase-1 {
  background: linear-gradient(135deg, #64748b, #94a3b8);
}

.phase-2 {
  background: linear-gradient(135deg, #7c3aed, #a78bfa);
}

.phase-3 {
  background: linear-gradient(135deg, #10b981, #34d399);
}

.timeline-content {
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 16px;
  padding: 32px;
}

.timeline-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.timeline-header h3 {
  font-size: 1.5rem;
  font-weight: 700;
}

.timeline-date {
  color: #94a3b8;
  font-size: 0.9rem;
}

.tech-stack {
  margin-bottom: 24px;
}

.tech-category {
  margin-bottom: 20px;
}

.tech-category h4 {
  color: #a78bfa;
  margin-bottom: 12px;
  font-size: 1rem;
}

.tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tech-tag {
  background: #0b0e14;
  border: 1px solid #2a3748;
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 0.85rem;
  color: #cbd5e1;
}

.tech-tag.vue {
  border-color: #42b883;
  color: #42b883;
}

.tech-tag.vite {
  border-color: #a78bfa;
  color: #a78bfa;
}

.tech-tag.git {
  border-color: #f05032;
  color: #f05032;
}

.tech-tag.github {
  border-color: #fff;
  color: #fff;
  background: #333;
}

.phase-description ul {
  list-style: none;
  padding: 0;
}

.phase-description li {
  padding: 8px 0;
  color: #cbd5e1;
  line-height: 1.6;
}

/* 技术卡片网格 */
.tech-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
}

.tech-card {
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 16px;
  overflow: hidden;
}

.tech-card-header {
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.tech-card-header i {
  font-size: 2rem;
}

.tech-card-header h3 {
  font-size: 1.3rem;
}

.frontend {
  background: linear-gradient(135deg, #42b88320, #35495e20);
  color: #42b883;
}

.backend {
  background: linear-gradient(135deg, #6db33f20, #00000020);
  color: #6db33f;
}

.database {
  background: linear-gradient(135deg, #00758f20, #f2911120);
  color: #00758f;
}

.tools {
  background: linear-gradient(135deg, #a78bfa20, #f472b620);
  color: #a78bfa;
}

.tech-card-body {
  padding: 24px;
}

.tech-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.tech-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 12px;
  border-bottom: 1px solid #2a3748;
}

.tech-item:last-child {
  border-bottom: none;
}

.tech-name {
  font-weight: 600;
  color: #edf2f7;
}

.tech-desc {
  color: #94a3b8;
  font-size: 0.85rem;
}

/* GitHub 计划 */
.github-plan {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.plan-step {
  display: flex;
  gap: 24px;
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 16px;
  padding: 32px;
}

.step-number {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #7c3aed, #a78bfa);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  font-weight: 700;
  flex-shrink: 0;
}

.step-content h3 {
  margin-bottom: 16px;
  font-size: 1.3rem;
}

.step-content ul {
  list-style: none;
  padding: 0;
}

.step-content li {
  padding: 8px 0;
  color: #cbd5e1;
  line-height: 1.6;
}

.code-block {
  background: #0b0e14;
  border: 1px solid #2a3748;
  border-radius: 8px;
  padding: 16px;
  overflow-x: auto;
}

.code-block code {
  color: #a78bfa;
  font-family: 'Courier New', monospace;
  font-size: 0.9rem;
  line-height: 1.6;
}

/* 项目结构 */
.project-structure {
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 16px;
  padding: 32px;
}

.structure-tree {
  color: #cbd5e1;
  font-family: 'Courier New', monospace;
  font-size: 0.9rem;
  line-height: 1.8;
  overflow-x: auto;
}
</style>
