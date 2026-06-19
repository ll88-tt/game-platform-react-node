<template>
  <div class="admin-container">
    <!-- 导航栏 -->
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

        <router-link to="/admin-dashboard" class="admin-btn dashboard-btn">
          <i class="fas fa-chart-pie"></i> 仪表盘
        </router-link>

        <router-link to="/admin-games" class="admin-btn games-admin-btn">
          <i class="fas fa-database"></i> 游戏管理
        </router-link>

        <router-link to="/admin-feedback" class="active admin-btn">
          <i class="fas fa-inbox"></i> 查看反馈 <i class="fas fa-arrow-right"></i>
        </router-link>

        <router-link to="/service" class="buy-now">
          订阅VIP <i class="fas fa-arrow-right"></i>
        </router-link>
      </nav>

      <UserAvatarMenu
          :show-admin-dashboard="userStore.isAdmin"
          :show-admin-games="userStore.isAdmin"
          :show-admin-feedback="userStore.isAdmin"
          show-home-link
      />
    </header>

    <!-- 权限检查中 -->
    <div v-if="checkingPermission" class="loading-container">
      <i class="fas fa-spinner fa-spin"></i>
      <p>正在验证权限...</p>
    </div>

    <!-- 无权限提示 -->
    <div v-else-if="!hasPermission" class="forbidden-container">
      <i class="fas fa-lock"></i>
      <h2>无权访问</h2>
      <p>此页面仅管理员可访问</p>
      <button class="btn-primary" @click="goToHome">返回首页</button>
    </div>

    <!-- 管理内容 -->
    <div v-else class="admin-content">
      <!-- 页面标题 -->
      <section class="page-hero">
        <h1 class="page-title">
          <i class="fas fa-inbox"></i> 反馈管理后台
        </h1>
        <p class="page-subtitle">查看和处理用户提交的问题反馈</p>
      </section>

      <!-- 统计卡片 -->
      <section class="stats-section">
        <div class="stat-card total">
          <div class="stat-icon"><i class="fas fa-envelope"></i></div>
          <div class="stat-info">
            <div class="stat-value">{{ totalCount }}</div>
            <div class="stat-label">总反馈数</div>
          </div>
        </div>

        <div class="stat-card pending">
          <div class="stat-icon"><i class="fas fa-clock"></i></div>
          <div class="stat-info">
            <div class="stat-value">{{ pendingCount }}</div>
            <div class="stat-label">待处理</div>
          </div>
        </div>

        <div class="stat-card resolved">
          <div class="stat-icon"><i class="fas fa-check-circle"></i></div>
          <div class="stat-info">
            <div class="stat-value">{{ resolvedCount }}</div>
            <div class="stat-label">已解决</div>
          </div>
        </div>
      </section>

      <!-- 筛选栏 -->
      <section class="filter-section">
        <div class="filter-bar">
          <button
              class="filter-btn"
              :class="{ active: currentFilter === 'all' }"
              @click="setFilter('all')"
          >
            全部
          </button>
          <button
              class="filter-btn"
              :class="{ active: currentFilter === 'pending' }"
              @click="setFilter('pending')"
          >
            待处理
          </button>
          <button
              class="filter-btn"
              :class="{ active: currentFilter === 'resolved' }"
              @click="setFilter('resolved')"
          >
            已解决
          </button>
        </div>

        <button class="refresh-btn" @click="loadFeedbacks">
          <i class="fas fa-sync-alt"></i> 刷新
        </button>
      </section>

      <!-- 反馈列表 -->
      <section class="feedback-list-section">
        <div v-if="loading" class="loading-state">
          <i class="fas fa-spinner fa-spin"></i>
          <p>加载中...</p>
        </div>

        <div v-else-if="filteredFeedbacks.length === 0" class="empty-state">
          <i class="fas fa-inbox"></i>
          <p>暂无反馈数据</p>
        </div>

        <div v-else class="feedback-list">
          <div
              v-for="feedback in filteredFeedbacks"
              :key="feedback.id"
              class="feedback-card"
              :class="{ resolved: feedback.isResolved }"
          >
            <div class="feedback-header">
              <span class="feedback-type">{{ getTypeName(feedback.problemType) }}</span>
              <span class="feedback-time">{{ formatTime(feedback.createTime) }}</span>
            </div>

            <div class="feedback-body">
              <div v-if="feedback.userName" class="feedback-user">
                <i class="fas fa-user"></i>
                {{ feedback.userName }}
                <span v-if="feedback.userEmail">({{ feedback.userEmail }})</span>
              </div>

              <div class="feedback-desc">{{ feedback.problemDesc }}</div>
            </div>

            <div class="feedback-footer">
              <span class="feedback-browser">
                <i class="fas fa-laptop"></i>
                {{ feedback.browserInfo || '未提供' }}
              </span>

              <div class="action-btns">
                <button
                    v-if="!feedback.isResolved"
                    class="action-btn resolve-btn"
                    @click="markAsResolved(feedback.id)"
                >
                  <i class="fas fa-check"></i> 标记解决
                </button>

                <button
                    class="action-btn delete-btn"
                    @click="confirmDelete(feedback.id)"
                >
                  <i class="fas fa-trash"></i> 删除
                </button>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>

    <!-- 删除确认弹窗 -->
    <div v-if="showDeleteConfirm" class="modal-overlay" @click="closeDeleteConfirm">
      <div class="modal" @click.stop>
        <div class="modal-icon warning">
          <i class="fas fa-exclamation-triangle"></i>
        </div>

        <h2>确认删除</h2>
        <p>确定删除此反馈？此操作不可恢复！</p>

        <div class="modal-footer">
          <button class="btn-secondary" @click="closeDeleteConfirm">取消</button>
          <button class="btn-danger" @click="executeDelete">
            <i class="fas fa-trash"></i> 确认删除
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import UserAvatarMenu from '../components/UserAvatarMenu.vue'
import { feedbackApi } from '../api/feedback'

const router = useRouter()
const userStore = useUserStore()

const showDropdown = ref(false)
const checkingPermission = ref(true)
const hasPermission = ref(false)
const loading = ref(false)
const allFeedbacks = ref([])
const currentFilter = ref('all')
const showDeleteConfirm = ref(false)
const deleteId = ref(null)

const permissionIcon = computed(() => {
  return '<i class="fas fa-crown" style="color: #f472b6; font-size: 0.8rem;"></i>'
})

const permissionName = computed(() => {
  return '管理员（永久VIP）'
})

const filteredFeedbacks = computed(() => {
  if (currentFilter.value === 'all') {
    return allFeedbacks.value
  } else if (currentFilter.value === 'pending') {
    return allFeedbacks.value.filter(f => !f.isResolved)
  } else if (currentFilter.value === 'resolved') {
    return allFeedbacks.value.filter(f => f.isResolved)
  }
  return allFeedbacks.value
})

const totalCount = computed(() => allFeedbacks.value.length)
const pendingCount = computed(() => allFeedbacks.value.filter(f => !f.isResolved).length)
const resolvedCount = computed(() => allFeedbacks.value.filter(f => f.isResolved).length)

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

  // 加载反馈数据
  await loadFeedbacks()
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
  showDropdown.value = false
  router.push('/home')
}

async function loadFeedbacks() {
  loading.value = true

  try {
    const response = await feedbackApi.getAllFeedbacks()
    allFeedbacks.value = response.data
  } catch (error) {
    console.error('加载反馈失败:', error)
    alert('加载失败，请刷新重试')
  } finally {
    loading.value = false
  }
}

function setFilter(filter) {
  currentFilter.value = filter
}

function getTypeName(type) {
  const typeMap = {
    'login': '登录/注册问题',
    'payment': '支付/订阅问题',
    'game': '游戏加载问题',
    'search': '搜索功能问题',
    'ui': '界面显示问题',
    'performance': '性能问题',
    'bug': '程序错误',
    'suggestion': '功能建议',
    'other': '其他问题'
  }
  return typeMap[type] || type
}

function formatTime(isoString) {
  const date = new Date(isoString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

async function markAsResolved(id) {
  if (!confirm('确定标记为已解决？')) return

  try {
    const response = await feedbackApi.markAsResolved(id)

    if (response.data.success) {
      await loadFeedbacks()
    } else {
      alert(response.data.message || '操作失败')
    }
  } catch (error) {
    console.error('标记解决失败:', error)
    alert('操作失败')
  }
}

function confirmDelete(id) {
  deleteId.value = id
  showDeleteConfirm.value = true
}

function closeDeleteConfirm() {
  showDeleteConfirm.value = false
  deleteId.value = null
}

async function executeDelete() {
  if (!deleteId.value) return

  try {
    const response = await feedbackApi.deleteFeedback(deleteId.value)

    if (response.data.success) {
      closeDeleteConfirm()
      await loadFeedbacks()
    } else {
      alert(response.data.message || '删除失败')
    }
  } catch (error) {
    console.error('删除失败:', error)
    alert('删除失败')
  }
}
</script>

<style scoped>
.admin-container {
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

.games-admin-btn {
  background: linear-gradient(145deg, #7c3aed, #6366f1);
  box-shadow: 0 6px 14px rgba(124, 58, 237, 0.3);
}

.dashboard-btn {
  background: linear-gradient(145deg, #0ea5e9, #6366f1);
  box-shadow: 0 6px 14px rgba(14, 165, 233, 0.3);
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

.dropdown-item.active {
  background: #7c3aed20;
  color: #a78bfa;
}

.divider {
  height: 1px;
  background: #2a3748;
  margin: 8px 0;
}

/* 加载和无权限 */
.loading-container,
.forbidden-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  gap: 24px;
}

.loading-container i {
  font-size: 3rem;
  color: #a78bfa;
}

.forbidden-container i {
  font-size: 5rem;
  color: #ef4444;
}

.forbidden-container h2 {
  font-size: 2rem;
}

.forbidden-container p {
  color: #94a3b8;
}

.btn-primary {
  padding: 12px 32px;
  background: linear-gradient(135deg, #7c3aed, #a78bfa);
  border: none;
  border-radius: 8px;
  color: white;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 14px rgba(124, 58, 237, 0.4);
}

/* 页面标题 */
.page-hero {
  margin-bottom: 48px;
}

.page-title {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 12px;
}

.page-subtitle {
  font-size: 1.1rem;
  color: #94a3b8;
}

/* 统计卡片 */
.stats-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.stat-card {
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  transition: all 0.2s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.2);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.8rem;
}

.total .stat-icon {
  background: linear-gradient(135deg, #a78bfa20, #7c3aed20);
  color: #a78bfa;
}

.pending .stat-icon {
  background: linear-gradient(135deg, #fbbf2420, #f59e0b20);
  color: #fbbf24;
}

.resolved .stat-icon {
  background: linear-gradient(135deg, #10b98120, #05966920);
  color: #10b981;
}

.stat-value {
  font-size: 2.5rem;
  font-weight: 700;
  line-height: 1;
  margin-bottom: 4px;
}

.stat-label {
  color: #94a3b8;
  font-size: 0.9rem;
}

/* 筛选栏 */
.filter-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.filter-bar {
  display: flex;
  gap: 12px;
}

.filter-btn {
  padding: 10px 20px;
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 8px;
  color: #cbd5e1;
  cursor: pointer;
  transition: all 0.2s;
}

.filter-btn:hover {
  border-color: #a78bfa;
}

.filter-btn.active {
  background: #7c3aed;
  border-color: #7c3aed;
  color: white;
}

.refresh-btn {
  padding: 10px 20px;
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 8px;
  color: #cbd5e1;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s;
}

.refresh-btn:hover {
  border-color: #a78bfa;
  color: #a78bfa;
}

/* 反馈列表 */
.feedback-list-section {
  min-height: 400px;
}

.loading-state,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  gap: 16px;
  color: #64748b;
}

.loading-state i {
  font-size: 3rem;
  color: #a78bfa;
}

.empty-state i {
  font-size: 4rem;
}

.feedback-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feedback-card {
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 16px;
  padding: 24px;
  transition: all 0.2s;
}

.feedback-card:hover {
  border-color: #7c3aed60;
}

.feedback-card.resolved {
  opacity: 0.6;
}

.feedback-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.feedback-type {
  display: inline-block;
  padding: 6px 12px;
  background: #7c3aed;
  border-radius: 6px;
  font-size: 0.85rem;
  font-weight: 600;
}

.feedback-time {
  color: #64748b;
  font-size: 0.9rem;
}

.feedback-body {
  margin-bottom: 16px;
}

.feedback-user {
  color: #94a3b8;
  font-size: 0.9rem;
  margin-bottom: 8px;
}

.feedback-desc {
  color: #edf2f7;
  line-height: 1.7;
  white-space: pre-wrap;
}

.feedback-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #2a3748;
}

.feedback-browser {
  color: #64748b;
  font-size: 0.85rem;
}

.action-btns {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.resolve-btn {
  background: #10b981;
  color: white;
}

.resolve-btn:hover {
  background: #059669;
}

.delete-btn {
  background: #ef4444;
  color: white;
}

.delete-btn:hover {
  background: #dc2626;
}

/* 删除确认弹窗 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.modal {
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 16px;
  padding: 48px;
  text-align: center;
  max-width: 450px;
  width: 90%;
}

.modal-icon {
  font-size: 5rem;
  margin-bottom: 24px;
}

.modal-icon.warning {
  color: #fbbf24;
}

.modal h2 {
  font-size: 1.8rem;
  margin-bottom: 12px;
}

.modal p {
  color: #94a3b8;
  margin-bottom: 32px;
}

.modal-footer {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.btn-secondary {
  padding: 12px 24px;
  background: #2a3748;
  border: none;
  border-radius: 8px;
  color: #cbd5e1;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-secondary:hover {
  background: #3a4758;
}

.btn-danger {
  padding: 12px 24px;
  background: #ef4444;
  border: none;
  border-radius: 8px;
  color: white;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-danger:hover {
  background: #dc2626;
  transform: translateY(-2px);
  box-shadow: 0 6px 14px rgba(239, 68, 68, 0.4);
}
</style>
