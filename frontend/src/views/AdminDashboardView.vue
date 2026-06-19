<template>
  <div class="admin-container">
    <header class="header">
      <div class="logo">
        <i class="fas fa-gamepad" style="margin-right: 6px; color: #c084fc;"></i>
        GAMEVILA
      </div>

      <nav class="nav-links">
        <router-link to="/home">首页</router-link>
        <router-link to="/about">关于</router-link>
        <router-link to="/service">服务</router-link>
        <router-link to="/contact">联系</router-link>

        <router-link to="/admin-dashboard" class="active admin-btn dashboard-btn">
          <i class="fas fa-chart-pie"></i> 仪表盘
        </router-link>

        <router-link to="/admin-games" class="admin-btn games-admin-btn">
          <i class="fas fa-database"></i> 游戏管理
        </router-link>

        <router-link to="/admin-feedback" class="admin-btn">
          <i class="fas fa-inbox"></i> 查看反馈
        </router-link>
      </nav>

      <UserAvatarMenu
          show-admin-dashboard
          show-admin-games
          show-admin-feedback
          show-home-link
      />
    </header>

    <div v-if="checkingPermission" class="loading-container">
      <i class="fas fa-spinner fa-spin"></i>
      <p>正在验证权限...</p>
    </div>

    <div v-else-if="!hasPermission" class="forbidden-container">
      <i class="fas fa-lock"></i>
      <h2>无权访问</h2>
      <p>此页面仅管理员可访问</p>
      <button class="btn-primary" @click="goToHome">返回首页</button>
    </div>

    <div v-else class="admin-content">
      <section class="page-hero">
        <h1 class="page-title">
          <i class="fas fa-chart-pie"></i> 管理员仪表盘
        </h1>
        <p class="page-subtitle">平台核心数据一览</p>
      </section>

      <section class="toolbar">
        <button class="refresh-btn" :disabled="loading" @click="loadStats">
          <i class="fas fa-sync-alt" :class="{ 'fa-spin': loading }"></i>
          {{ loading ? '刷新中...' : '刷新数据' }}
        </button>
        <span v-if="lastUpdated" class="last-updated">更新于 {{ lastUpdated }}</span>
      </section>

      <div v-if="loading && !stats" class="loading-state">
        <i class="fas fa-spinner fa-spin"></i>
        <p>加载统计数据...</p>
      </div>

      <section v-else class="stats-section">
        <div class="stat-card users">
          <div class="stat-icon"><i class="fas fa-users"></i></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats?.userCount ?? 0 }}</div>
            <div class="stat-label">用户总数</div>
          </div>
        </div>

        <div class="stat-card vip">
          <div class="stat-icon"><i class="fas fa-crown"></i></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats?.vipCount ?? 0 }}</div>
            <div class="stat-label">有效 VIP 用户</div>
          </div>
        </div>

        <div class="stat-card orders">
          <div class="stat-icon"><i class="fas fa-receipt"></i></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats?.todayOrderCount ?? 0 }}</div>
            <div class="stat-label">今日已支付订单</div>
          </div>
        </div>

        <div class="stat-card feedback">
          <div class="stat-icon"><i class="fas fa-inbox"></i></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats?.pendingFeedbackCount ?? 0 }}</div>
            <div class="stat-label">待处理反馈</div>
          </div>
        </div>
      </section>

      <section class="quick-links">
        <h2><i class="fas fa-bolt"></i> 快捷入口</h2>
        <div class="link-grid">
          <router-link to="/admin-games" class="link-card">
            <i class="fas fa-database"></i>
            <span>游戏管理</span>
          </router-link>
          <router-link to="/admin-feedback" class="link-card">
            <i class="fas fa-inbox"></i>
            <span>反馈管理</span>
            <span v-if="stats?.pendingFeedbackCount" class="link-badge">{{ stats.pendingFeedbackCount }}</span>
          </router-link>
          <router-link to="/admin-games" class="link-card" @click="goToMembershipTab">
            <i class="fas fa-tags"></i>
            <span>会员定价</span>
          </router-link>
          <router-link to="/service" class="link-card">
            <i class="fas fa-store"></i>
            <span>服务页预览</span>
          </router-link>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import UserAvatarMenu from '../components/UserAvatarMenu.vue'
import { adminDashboardApi } from '../api/adminDashboard'

const router = useRouter()
const userStore = useUserStore()

const showDropdown = ref(false)
const checkingPermission = ref(true)
const hasPermission = ref(false)
const loading = ref(false)
const stats = ref(null)
const lastUpdated = ref('')

onMounted(async () => {
  await checkPermission()
})

async function checkPermission() {
  checkingPermission.value = true
  await userStore.checkLoginStatus()

  if (!userStore.isLoggedIn) {
    alert('请先登录')
    router.push('/home')
    return
  }

  if (!userStore.isAdmin) {
    hasPermission.value = false
    checkingPermission.value = false
    return
  }

  hasPermission.value = true
  checkingPermission.value = false
  await loadStats()
}

async function loadStats() {
  loading.value = true
  try {
    const response = await adminDashboardApi.getStats()
    if (response.data.success) {
      stats.value = response.data.stats
      lastUpdated.value = new Date().toLocaleString('zh-CN')
    } else {
      alert(response.data.message || '加载失败')
    }
  } catch (error) {
    console.error('加载仪表盘失败:', error)
    alert(error.response?.status === 403 ? '无权访问' : '加载统计数据失败')
  } finally {
    loading.value = false
  }
}

function toggleDropdown() {
  showDropdown.value = !showDropdown.value
}

async function handleLogout() {
  await userStore.logout()
  showDropdown.value = false
  router.push('/home')
}

function goToHome() {
  router.push('/home')
}

function goToMembershipTab() {
  sessionStorage.setItem('adminGamesTab', 'membership')
}
</script>

<style scoped>
.admin-container {
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
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.nav-links a {
  color: #cbd5e1;
  text-decoration: none;
}

.nav-links a:hover,
.nav-links a.active { color: #a78bfa; }

.admin-btn {
  background: linear-gradient(145deg, #ef4444, #f97316);
  padding: 8px 16px;
  border-radius: 40px;
  color: white !important;
  font-weight: 600;
  font-size: 0.9rem;
}

.dashboard-btn {
  background: linear-gradient(145deg, #0ea5e9, #6366f1);
  box-shadow: 0 6px 14px rgba(14, 165, 233, 0.3);
}

.games-admin-btn {
  background: linear-gradient(145deg, #7c3aed, #6366f1);
}

.avatar-wrapper { position: relative; }

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
  z-index: 1000;
}

.user-info { padding: 16px; border-bottom: 1px solid #2a3748; }

.dropdown-item {
  width: 100%;
  padding: 12px 16px;
  background: none;
  border: none;
  color: #cbd5e1;
  text-align: left;
  cursor: pointer;
}

.dropdown-item:hover { background: #1e2a3a; }

.divider { height: 1px; background: #2a3748; margin: 8px 0; }

.loading-container,
.forbidden-container,
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 50vh;
  gap: 16px;
  color: #94a3b8;
}

.loading-container i,
.loading-state i { font-size: 2.5rem; color: #a78bfa; }

.forbidden-container i { font-size: 4rem; color: #ef4444; }

.btn-primary {
  padding: 10px 20px;
  background: linear-gradient(135deg, #7c3aed, #a78bfa);
  border: none;
  border-radius: 8px;
  color: white;
  font-weight: 600;
  cursor: pointer;
}

.page-hero { margin-bottom: 24px; }

.page-title {
  font-size: 2.2rem;
  font-weight: 700;
  margin-bottom: 8px;
}

.page-subtitle { color: #94a3b8; }

.toolbar {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 28px;
}

.refresh-btn {
  padding: 10px 16px;
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 8px;
  color: #cbd5e1;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.refresh-btn:disabled { opacity: 0.6; cursor: not-allowed; }

.last-updated { color: #64748b; font-size: 0.9rem; }

.stats-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.stat-card {
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: transform 0.2s, box-shadow 0.2s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.25);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.6rem;
}

.stat-card.users .stat-icon { background: #6366f130; color: #818cf8; }
.stat-card.vip .stat-icon { background: #f472b630; color: #f472b6; }
.stat-card.orders .stat-icon { background: #10b98130; color: #34d399; }
.stat-card.feedback .stat-icon { background: #f59e0b30; color: #fbbf24; }

.stat-value {
  font-size: 2rem;
  font-weight: 700;
  line-height: 1.2;
}

.stat-label {
  color: #94a3b8;
  font-size: 0.9rem;
  margin-top: 4px;
}

.quick-links h2 {
  font-size: 1.3rem;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.link-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 16px;
}

.link-card {
  position: relative;
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 12px;
  padding: 24px;
  text-align: center;
  color: #cbd5e1;
  text-decoration: none;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  transition: all 0.2s;
}

.link-card i { font-size: 1.8rem; color: #a78bfa; }

.link-card:hover {
  border-color: #7c3aed;
  transform: translateY(-2px);
  color: #edf2f7;
}

.link-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: #ef4444;
  color: white;
  font-size: 0.75rem;
  font-weight: 700;
  padding: 2px 8px;
  border-radius: 20px;
}
</style>
