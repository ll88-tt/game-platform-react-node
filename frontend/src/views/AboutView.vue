<template>
  <div class="about-container">
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

        <router-link v-if="userStore.isAdmin" to="/admin-dashboard" class="admin-btn dashboard-btn">
          <i class="fas fa-chart-pie"></i> 仪表盘
        </router-link>
        <router-link v-if="userStore.isAdmin" to="/admin-games" class="admin-btn games-admin-btn">
          <i class="fas fa-database"></i> 游戏管理
        </router-link>

        <router-link to="/service" class="buy-now">
          订阅 VIP <i class="fas fa-arrow-right"></i>
        </router-link>
      </nav>

      <UserAvatarMenu
        :show-admin-dashboard="userStore.isAdmin"
        :show-admin-games="userStore.isAdmin"
        :show-admin-feedback="userStore.isAdmin"
        show-browse-history
        @login="goToHomeLogin"
        @browse-history="openHistoryPanel"
      />
    </header>

    <!-- Hero -->
    <section class="hero">
      <div class="hero-badge"><i class="fas fa-sparkles"></i> Perfect Version · Vue 3 全栈实践</div>
      <h1 class="hero-title">关于 <span>GAMEVILA</span></h1>
      <p class="hero-desc">
        一个从 HTML 原型演进到 Vue 3 + Spring Boot 的现代化游戏平台。
        涵盖智能搜索、VIP 订阅、用户中心、管理后台与云端数据同步的完整业务闭环。
      </p>
      <div class="hero-actions">
        <a href="http://120.79.160.66" target="_blank" rel="noopener" class="btn-primary">
          <i class="fas fa-globe"></i> 在线体验
        </a>
        <a
          href="https://github.com/ll88-tt/game-platform-react-node"
          target="_blank"
          rel="noopener"
          class="btn-secondary"
        >
          <i class="fab fa-github"></i> GitHub 仓库
        </a>
      </div>
    </section>

    <!-- 数据概览 -->
    <section class="stats-row">
      <div v-for="stat in stats" :key="stat.label" class="stat-card">
        <i :class="stat.icon"></i>
        <div>
          <strong>{{ stat.value }}</strong>
          <span>{{ stat.label }}</span>
        </div>
      </div>
    </section>

    <!-- 平台能力 -->
    <section class="content-section">
      <div class="section-header">
        <h2><i class="fas fa-th-large"></i> 平台能力</h2>
        <p>当前 perfect 版本已实现的核心模块</p>
      </div>

      <div class="capability-grid">
        <article v-for="item in capabilities" :key="item.title" class="capability-card">
          <div class="capability-icon" :class="item.tone">
            <i :class="item.icon"></i>
          </div>
          <h3>{{ item.title }}</h3>
          <p>{{ item.desc }}</p>
          <ul>
            <li v-for="point in item.points" :key="point">{{ point }}</li>
          </ul>
        </article>
      </div>
    </section>

    <!-- 技术演进 -->
    <section class="content-section">
      <div class="section-header">
        <h2><i class="fas fa-code-branch"></i> 技术演进</h2>
        <p>从快速原型到工程化全栈平台的三个阶段</p>
      </div>

      <div class="evolution-grid">
        <article v-for="phase in phases" :key="phase.title" class="phase-card" :class="phase.tone">
          <div class="phase-head">
            <span class="phase-label">{{ phase.label }}</span>
            <h3>{{ phase.title }}</h3>
          </div>
          <div class="phase-tags">
            <span v-for="tag in phase.tags" :key="tag">{{ tag }}</span>
          </div>
          <ul>
            <li v-for="line in phase.lines" :key="line">{{ line }}</li>
          </ul>
        </article>
      </div>
    </section>

    <!-- 架构设计 -->
    <section class="content-section">
      <div class="section-header">
        <h2><i class="fas fa-sitemap"></i> 架构设计</h2>
        <p>前后端分离 + Redis Session + MySQL 持久化</p>
      </div>

      <div class="arch-flow">
        <div class="arch-node frontend">Vue 3 SPA<br><small>Router · Pinia · Axios</small></div>
        <div class="arch-arrow"><i class="fas fa-arrows-alt-h"></i></div>
        <div class="arch-node backend">Spring Boot API<br><small>Security · JPA · Scheduled</small></div>
        <div class="arch-arrow"><i class="fas fa-arrows-alt-h"></i></div>
        <div class="arch-node data">MySQL + Redis<br><small>业务数据 · Session</small></div>
      </div>

      <div class="design-notes">
        <div class="note-card">
          <h4><i class="fas fa-bolt"></i> Session 与状态</h4>
          <p>登录态存入 Redis，减轻 MySQL 压力，支持分布式部署与服务重启后会话恢复。</p>
        </div>
        <div class="note-card">
          <h4><i class="fas fa-cloud"></i> 个人数据云端化</h4>
          <p>收藏与浏览历史写入数据库，登录后跨设备同步；未登录时浏览记录暂存本地，登录自动合并。</p>
        </div>
        <div class="note-card">
          <h4><i class="fas fa-clock"></i> VIP 生命周期</h4>
          <p>订阅支付写入订单表，定时任务每小时扫描到期 VIP 并自动降级，登录时即时校验权限。</p>
        </div>
      </div>
    </section>

    <!-- 技术栈 -->
    <section class="content-section">
      <div class="section-header">
        <h2><i class="fas fa-layer-group"></i> 技术栈</h2>
      </div>

      <div class="stack-grid">
        <div v-for="stack in techStacks" :key="stack.title" class="stack-card">
          <h3><i :class="stack.icon"></i> {{ stack.title }}</h3>
          <div class="stack-items">
            <span v-for="item in stack.items" :key="item">{{ item }}</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 页面地图 -->
    <section class="content-section">
      <div class="section-header">
        <h2><i class="fas fa-map"></i> 站点地图</h2>
      </div>

      <div class="route-grid">
        <router-link v-for="route in siteRoutes" :key="route.path" :to="route.path" class="route-card">
          <i :class="route.icon"></i>
          <div>
            <strong>{{ route.name }}</strong>
            <span>{{ route.path }}</span>
          </div>
          <span v-if="route.badge" class="route-badge">{{ route.badge }}</span>
        </router-link>
      </div>
    </section>

    <!-- 关于作者 -->
    <section class="author-section">
      <div class="author-card">
        <div class="author-avatar"><i class="fas fa-user-graduate"></i></div>
        <div>
          <h2>关于作者</h2>
          <p class="author-school">广州大学 · 软件工程 · 本科在读（2023–2027）</p>
          <p class="author-bio">
            GAMEVILA 是个人全栈技术实践项目，目标是在真实业务场景中掌握前后端分离、
            会员体系设计、权限控制与工程化协作。欢迎通过 GitHub Issue 交流或提出建议。
          </p>
          <div class="author-links">
            <a href="https://github.com/ll88-tt/game-platform-react-node" target="_blank" rel="noopener">
              <i class="fab fa-github"></i> ll88-tt/game-platform-react-node
            </a>
            <router-link to="/contact"><i class="fas fa-envelope"></i> 联系反馈</router-link>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import UserAvatarMenu from '../components/UserAvatarMenu.vue'

const router = useRouter()
const userStore = useUserStore()

const stats = [
  { icon: 'fas fa-file-code', value: '9+', label: 'Vue 页面组件' },
  { icon: 'fas fa-plug', value: '12+', label: 'REST 控制器' },
  { icon: 'fas fa-crown', value: '4', label: 'VIP 订阅方案' },
  { icon: 'fas fa-server', value: '1', label: '云服务器部署' }
]

const capabilities = [
  {
    icon: 'fas fa-search',
    tone: 'purple',
    title: '智能搜索',
    desc: 'Trie 树 + N-Gram 模糊匹配，数据库 LIKE 兜底。',
    points: ['实时搜索建议', '热门搜索统计', '本地搜索历史']
  },
  {
    icon: 'fas fa-crown',
    tone: 'pink',
    title: 'VIP 会员体系',
    desc: '多层级订阅，模拟支付全流程。',
    points: ['月度 / 季度 / 年度 / 终身', '订单记录与续费', '到期自动降级定时任务']
  },
  {
    icon: 'fas fa-user-cog',
    tone: 'blue',
    title: '用户中心',
    desc: '账号与个人数据统一管理。',
    points: ['修改密码', '收藏与浏览历史', 'VIP 状态与订单']
  },
  {
    icon: 'fas fa-shield-alt',
    tone: 'green',
    title: '管理后台',
    desc: '管理员专属运营能力。',
    points: ['数据看板', '游戏 CRUD 与封面上传', '套餐与反馈管理']
  },
  {
    icon: 'fas fa-heart',
    tone: 'rose',
    title: '收藏系统',
    desc: '登录用户收藏持久化到 MySQL。',
    points: ['首页一键收藏', '用户中心管理', 'VIP 游戏权限提示']
  },
  {
    icon: 'fas fa-history',
    tone: 'amber',
    title: '浏览历史',
    desc: '云端同步，与 VIP 容量绑定。',
    points: ['登录后跨设备同步', '未登录 localStorage 暂存', '登录自动合并上传']
  }
]

const phases = [
  {
    label: 'Phase 1',
    title: 'HTML + 原生 JS',
    tone: 'muted',
    tags: ['HTML5', 'CSS3', 'Spring Boot', 'MySQL', 'Redis'],
    lines: [
      '✅ 快速验证业务原型',
      '❌ 页面重复、状态分散',
      '❌ 无 SPA，维护成本高'
    ]
  },
  {
    label: 'Phase 2 · 当前',
    title: 'Vue 3 + Vite 重构',
    tone: 'active',
    tags: ['Vue 3', 'Pinia', 'Vue Router', 'Axios', 'Vite 5'],
    lines: [
      '✅ 组件化与路由守卫',
      '✅ 用户中心 / 订阅 / 管理后台',
      '✅ 收藏、浏览历史云端同步'
    ]
  },
  {
    label: 'Phase 3 · 进行中',
    title: 'Git 协作与部署',
    tone: 'future',
    tags: ['GitHub', 'perfect-verson', '云服务器', 'README'],
    lines: [
      '✅ 代码托管与分支管理',
      '🚧 CI/CD 自动化（规划中）',
      '🚧 Docker 容器化（规划中）'
    ]
  }
]

const techStacks = [
  { title: '前端', icon: 'fas fa-laptop-code', items: ['Vue 3', 'Vite', 'Vue Router', 'Pinia', 'Axios'] },
  { title: '后端', icon: 'fas fa-server', items: ['Spring Boot 3', 'Spring Security', 'JPA', 'Scheduled', 'BCrypt'] },
  { title: '存储', icon: 'fas fa-database', items: ['MySQL 8', 'Redis', 'Spring Session'] },
  { title: '工具', icon: 'fas fa-wrench', items: ['Maven', 'npm', 'JDK 17', 'Git', 'Font Awesome'] }
]

const siteRoutes = [
  { path: '/home', name: '首页', icon: 'fas fa-home' },
  { path: '/service', name: 'VIP 订阅', icon: 'fas fa-crown' },
  { path: '/user-center', name: '用户中心', icon: 'fas fa-user-cog', badge: '登录' },
  { path: '/subscription', name: '我的订阅', icon: 'fas fa-receipt', badge: '登录' },
  { path: '/contact', name: '联系反馈', icon: 'fas fa-envelope' },
  { path: '/admin-dashboard', name: '管理看板', icon: 'fas fa-chart-pie', badge: '管理员' },
  { path: '/admin-games', name: '游戏管理', icon: 'fas fa-database', badge: '管理员' }
]

onMounted(async () => {
  await userStore.checkLoginStatus()
})

function goToHomeLogin() {
  router.push('/home')
}

function openHistoryPanel() {
  if (userStore.isLoggedIn) {
    router.push({ path: '/user-center', query: { tab: 'history' } })
  } else {
    router.push({ path: '/home', query: { history: '1' } })
  }
}
</script>

<style scoped>
.about-container {
  max-width: 1200px;
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
  flex-wrap: wrap;
  gap: 16px;
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
  gap: 16px;
  align-items: center;
  flex-wrap: wrap;
}

.nav-links a {
  color: #cbd5e1;
  text-decoration: none;
}

.nav-links a:hover,
.nav-links a.active {
  color: #a78bfa;
}

.admin-btn,
.buy-now {
  padding: 8px 16px;
  border-radius: 40px;
  color: white !important;
  font-weight: 600;
  font-size: 0.9rem;
}

.admin-btn { background: linear-gradient(145deg, #ef4444, #f97316); }
.dashboard-btn { background: linear-gradient(145deg, #6366f1, #8b5cf6); }
.games-admin-btn { background: linear-gradient(145deg, #0ea5e9, #06b6d4); }
.buy-now { background: linear-gradient(145deg, #7c3aed, #a78bfa); }

.hero {
  text-align: center;
  padding: 48px 24px 56px;
  margin-bottom: 32px;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 18px;
  border-radius: 999px;
  background: #7c3aed25;
  border: 1px solid #7c3aed50;
  color: #c4b5fd;
  font-size: 0.9rem;
  margin-bottom: 20px;
}

.hero-title {
  font-size: clamp(2rem, 5vw, 3.2rem);
  font-weight: 800;
  margin-bottom: 16px;
}

.hero-title span {
  background: linear-gradient(135deg, #a78bfa, #f472b6);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.hero-desc {
  max-width: 720px;
  margin: 0 auto 28px;
  color: #94a3b8;
  line-height: 1.8;
  font-size: 1.05rem;
}

.hero-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
}

.btn-primary,
.btn-secondary {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 22px;
  border-radius: 12px;
  text-decoration: none;
  font-weight: 600;
  transition: transform 0.15s, box-shadow 0.15s;
}

.btn-primary {
  background: linear-gradient(135deg, #7c3aed, #a78bfa);
  color: white;
}

.btn-secondary {
  background: #161e2a;
  border: 1px solid #334155;
  color: #e2e8f0;
}

.btn-primary:hover,
.btn-secondary:hover {
  transform: translateY(-2px);
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 48px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 22px;
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 14px;
}

.stat-card i {
  font-size: 1.6rem;
  color: #a78bfa;
}

.stat-card strong {
  display: block;
  font-size: 1.5rem;
}

.stat-card span {
  color: #94a3b8;
  font-size: 0.9rem;
}

.content-section {
  margin-bottom: 56px;
}

.section-header {
  margin-bottom: 24px;
}

.section-header h2 {
  font-size: 1.6rem;
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 6px;
}

.section-header p {
  color: #94a3b8;
}

.capability-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.capability-card {
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 16px;
  padding: 24px;
}

.capability-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  font-size: 1.2rem;
}

.capability-icon.purple { background: #7c3aed30; color: #a78bfa; }
.capability-icon.pink { background: #f472b630; color: #f472b6; }
.capability-icon.blue { background: #3b82f630; color: #60a5fa; }
.capability-icon.green { background: #10b98130; color: #34d399; }
.capability-icon.rose { background: #fb718530; color: #fb7185; }
.capability-icon.amber { background: #f59e0b30; color: #fbbf24; }

.capability-card h3 {
  margin-bottom: 8px;
}

.capability-card > p {
  color: #94a3b8;
  font-size: 0.92rem;
  line-height: 1.6;
  margin-bottom: 12px;
}

.capability-card ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.capability-card li {
  color: #cbd5e1;
  font-size: 0.88rem;
  padding: 4px 0;
}

.capability-card li::before {
  content: '· ';
  color: #a78bfa;
}

.evolution-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.phase-card {
  border-radius: 16px;
  padding: 24px;
  border: 1px solid #2a3748;
}

.phase-card.muted { background: #111827; }
.phase-card.active {
  background: linear-gradient(160deg, #161e2a, #1a103550);
  border-color: #7c3aed60;
}
.phase-card.future {
  background: linear-gradient(160deg, #161e2a, #0f292550);
  border-color: #10b98150;
}

.phase-label {
  font-size: 0.8rem;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.phase-head h3 {
  margin: 6px 0 14px;
  font-size: 1.2rem;
}

.phase-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 14px;
}

.phase-tags span {
  font-size: 0.78rem;
  padding: 4px 10px;
  border-radius: 999px;
  background: #0f172a;
  border: 1px solid #334155;
  color: #cbd5e1;
}

.phase-card ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.phase-card li {
  color: #cbd5e1;
  font-size: 0.9rem;
  line-height: 1.7;
}

.arch-flow {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 24px;
}

.arch-node {
  min-width: 160px;
  padding: 20px 16px;
  text-align: center;
  border-radius: 14px;
  font-weight: 600;
  line-height: 1.5;
}

.arch-node small {
  font-weight: 400;
  color: inherit;
  opacity: 0.85;
}

.arch-node.frontend { background: #42b88325; border: 1px solid #42b88360; color: #6ee7b7; }
.arch-node.backend { background: #6db33f25; border: 1px solid #6db33f60; color: #86efac; }
.arch-node.data { background: #0ea5e925; border: 1px solid #0ea5e960; color: #7dd3fc; }

.arch-arrow {
  color: #64748b;
  font-size: 1.2rem;
}

.design-notes {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
}

.note-card {
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 14px;
  padding: 20px;
}

.note-card h4 {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  color: #a78bfa;
}

.note-card p {
  color: #94a3b8;
  font-size: 0.92rem;
  line-height: 1.7;
  margin: 0;
}

.stack-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;
}

.stack-card {
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 14px;
  padding: 20px;
}

.stack-card h3 {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 14px;
  font-size: 1.05rem;
}

.stack-items {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.stack-items span {
  font-size: 0.82rem;
  padding: 5px 10px;
  border-radius: 8px;
  background: #0f172a;
  color: #cbd5e1;
}

.route-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 12px;
}

.route-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px 18px;
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 12px;
  text-decoration: none;
  color: inherit;
  transition: border-color 0.15s, transform 0.15s;
}

.route-card:hover {
  border-color: #7c3aed;
  transform: translateY(-2px);
}

.route-card i {
  font-size: 1.2rem;
  color: #a78bfa;
  width: 24px;
  text-align: center;
}

.route-card strong {
  display: block;
}

.route-card span {
  color: #64748b;
  font-size: 0.85rem;
}

.route-badge {
  margin-left: auto;
  font-size: 0.75rem;
  padding: 3px 8px;
  border-radius: 999px;
  background: #7c3aed30;
  color: #c4b5fd;
}

.author-section {
  margin-bottom: 32px;
}

.author-card {
  display: flex;
  gap: 24px;
  align-items: flex-start;
  padding: 32px;
  background: linear-gradient(135deg, #161e2a, #1a103540);
  border: 1px solid #7c3aed40;
  border-radius: 20px;
}

.author-avatar {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: linear-gradient(135deg, #7c3aed, #f472b6);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.8rem;
  color: white;
  flex-shrink: 0;
}

.author-card h2 {
  margin-bottom: 8px;
}

.author-school {
  color: #a78bfa;
  margin-bottom: 12px;
}

.author-bio {
  color: #94a3b8;
  line-height: 1.8;
  margin-bottom: 16px;
}

.author-links {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.author-links a {
  color: #cbd5e1;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.author-links a:hover {
  color: #a78bfa;
}

@media (max-width: 768px) {
  .header { flex-direction: column; align-items: flex-start; }
  .author-card { flex-direction: column; }
  .arch-flow { flex-direction: column; }
  .arch-arrow { transform: rotate(90deg); }
}
</style>
