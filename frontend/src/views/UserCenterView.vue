<template>
  <div class="user-center-container">
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
        <router-link to="/user-center" class="active buy-now">用户中心</router-link>
      </nav>

      <UserAvatarMenu show-home-link />
    </header>

    <section class="page-hero">
      <h1 class="page-title"><i class="fas fa-user-cog"></i> 用户中心</h1>
      <p class="page-subtitle">管理账号安全、订阅与收藏</p>
    </section>

    <div v-if="subscription" class="vip-status-card" :class="vipCardClass">
      <div class="vip-status-main">
        <i class="fas fa-crown"></i>
        <div>
          <span class="vip-label">{{ vipPlanLabel }}</span>
          <span class="vip-expiry">{{ vipExpiryLabel }}</span>
        </div>
      </div>
      <div class="vip-status-meta">
        <span v-if="subscription.vipActive && subscription.daysRemaining != null">
          剩余 {{ subscription.daysRemaining }} 天
        </span>
        <span v-else-if="subscription.vipActive && subscription.vipExpiryTime === '永久'">永久有效</span>
        <span v-else-if="subscription.vipStatus">{{ subscription.vipStatus }}</span>
        <router-link v-if="!subscription.vipActive && !subscription.isAdmin" to="/service" class="renew-link">
          立即开通 VIP
        </router-link>
      </div>
    </div>

    <div class="tabs">
      <button class="tab-btn" :class="{ active: activeTab === 'password' }" @click="activeTab = 'password'">
        <i class="fas fa-key"></i> 修改密码
      </button>
      <button class="tab-btn" :class="{ active: activeTab === 'orders' }" @click="switchToOrders">
        <i class="fas fa-receipt"></i> 订单记录
        <span v-if="orders.length" class="tab-badge">{{ orders.length }}</span>
      </button>
      <button class="tab-btn" :class="{ active: activeTab === 'favorites' }" @click="switchToFavorites">
        <i class="fas fa-heart"></i> 我的收藏
        <span v-if="favorites.length" class="tab-badge">{{ favorites.length }}</span>
      </button>
      <button class="tab-btn" :class="{ active: activeTab === 'history' }" @click="switchToHistory">
        <i class="fas fa-history"></i> 浏览记录
        <span v-if="browseHistory.length" class="tab-badge">{{ browseHistory.length }}</span>
      </button>
    </div>

    <!-- 修改密码 -->
    <section v-if="activeTab === 'password'" class="panel">
      <h2><i class="fas fa-shield-alt"></i> 修改密码</h2>
      <p class="panel-desc">为保障账号安全，请先输入当前密码，再设置新密码（至少 6 位）。</p>

      <form class="password-form" @submit.prevent="submitPasswordChange">
        <div class="form-group">
          <label>当前密码</label>
          <input v-model="passwordForm.oldPassword" type="password" autocomplete="current-password" placeholder="请输入当前密码" />
        </div>
        <div class="form-group">
          <label>新密码</label>
          <input v-model="passwordForm.newPassword" type="password" autocomplete="new-password" placeholder="至少 6 位" />
        </div>
        <div class="form-group">
          <label>确认新密码</label>
          <input v-model="passwordForm.confirmPassword" type="password" autocomplete="new-password" placeholder="再次输入新密码" />
        </div>

        <p v-if="passwordMessage" class="form-message" :class="passwordSuccess ? 'success' : 'error'">
          {{ passwordMessage }}
        </p>

        <button class="btn-primary" type="submit" :disabled="changingPassword">
          <i v-if="changingPassword" class="fas fa-spinner fa-spin"></i>
          {{ changingPassword ? '提交中...' : '确认修改' }}
        </button>
      </form>
    </section>

    <!-- 订单记录 -->
    <section v-else-if="activeTab === 'orders'" class="panel">
      <div class="panel-header">
        <div>
          <h2><i class="fas fa-receipt"></i> 订单记录</h2>
          <p class="panel-desc">共 {{ orders.length }} 笔订单</p>
        </div>
        <button class="btn-secondary" @click="loadOrders">
          <i class="fas fa-sync-alt"></i> 刷新
        </button>
      </div>

      <div v-if="loadingOrders" class="loading-state">
        <i class="fas fa-spinner fa-spin"></i>
        <p>加载中...</p>
      </div>

      <div v-else-if="orders.length === 0" class="empty-state">
        <i class="fas fa-file-invoice"></i>
        <p>暂无订单记录</p>
        <router-link to="/service" class="btn-primary" style="margin-top: 16px; display: inline-flex; text-decoration: none;">
          去开通 VIP
        </router-link>
      </div>

      <div v-else class="order-table-wrap">
        <table class="order-table">
          <thead>
            <tr>
              <th>订单号</th>
              <th>套餐</th>
              <th>金额</th>
              <th>状态</th>
              <th>下单时间</th>
              <th>到期时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="order in orders" :key="order.id">
              <td><code class="order-no">#{{ formatOrderNo(order.id) }}</code></td>
              <td>
                <span class="plan-name">{{ order.planName }}</span>
                <span class="plan-code">{{ order.planCode }}</span>
              </td>
              <td class="amount">¥{{ formatPrice(order.amount) }}</td>
              <td>
                <span class="badge" :class="order.status === 'PAID' ? 'paid' : 'pending'">
                  {{ statusLabel(order.status) }}
                </span>
              </td>
              <td>{{ formatDateTime(order.createdAt) }}</td>
              <td>{{ formatExpiry(order.vipExpiryAfter) }}</td>
              <td class="order-action">
                <button
                  v-if="order.status === 'PENDING'"
                  class="btn-pay"
                  :disabled="payingOrderId === order.id"
                  @click="continuePay(order)"
                >
                  <i v-if="payingOrderId === order.id" class="fas fa-spinner fa-spin"></i>
                  <i v-else class="fas fa-wallet"></i>
                  继续支付
                </button>
                <span v-else>-</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <!-- 浏览记录 -->
    <section v-else-if="activeTab === 'history'" class="panel">
      <div class="panel-header">
        <div>
          <h2><i class="fas fa-history"></i> 浏览记录</h2>
          <p class="panel-desc">
            共 {{ browseHistory.length }} 条记录
            <span v-if="libraryLimits">（上限 {{ libraryLimits.maxBrowseHistory }}，云端同步）</span>
          </p>
        </div>
        <div class="panel-actions">
          <button class="btn-secondary" @click="loadBrowseHistory">
            <i class="fas fa-sync-alt"></i> 刷新
          </button>
          <button v-if="browseHistory.length" class="btn-secondary danger" @click="clearBrowseHistory">
            <i class="fas fa-trash-alt"></i> 清空
          </button>
        </div>
      </div>

      <div v-if="loadingHistory" class="loading-state">
        <i class="fas fa-spinner fa-spin"></i>
        <p>加载中...</p>
      </div>

      <div v-else-if="browseHistory.length === 0" class="empty-state">
        <i class="fas fa-history"></i>
        <p>还没有浏览记录</p>
        <button class="btn-primary" style="margin-top: 16px" @click="router.push('/home')">去首页逛逛</button>
      </div>

      <div v-else class="history-list">
        <div v-for="item in browseHistory" :key="item.historyId || item.gameId" class="history-row">
          <div class="history-main" @click="openHistoryGame(item)">
            <div class="history-title">
              {{ item.name }}
              <span v-if="item.vipOnly" class="vip-tag-inline">VIP</span>
            </div>
            <div class="history-meta">
              <span><i class="fas fa-clock"></i> {{ formatTime(item.viewedAt) }}</span>
              <span v-if="item.category"><i class="fas fa-tag"></i> {{ item.category }}</span>
            </div>
          </div>
          <div class="history-actions">
            <button class="btn-play small" :disabled="!item.canAccess" @click="openHistoryGame(item)">
              <i :class="item.canAccess ? 'fas fa-play' : 'fas fa-lock'"></i>
            </button>
            <button class="btn-unfavorite small" @click="removeHistoryItem(item)">
              <i class="fas fa-times"></i>
            </button>
          </div>
        </div>
      </div>
    </section>

    <!-- 我的收藏 -->
    <section v-else-if="activeTab === 'favorites'" class="panel">
      <div class="panel-header">
        <div>
          <h2><i class="fas fa-heart"></i> 我的收藏</h2>
          <p class="panel-desc">
            共 {{ favorites.length }} 款游戏
            <span v-if="libraryLimits">（上限 {{ libraryLimits.maxFavorites }} 款）</span>
          </p>
        </div>
        <button class="btn-secondary" @click="loadFavorites">
          <i class="fas fa-sync-alt"></i> 刷新
        </button>
      </div>

      <div v-if="loadingFavorites" class="loading-state">
        <i class="fas fa-spinner fa-spin"></i>
        <p>加载中...</p>
      </div>

      <div v-else-if="favorites.length === 0" class="empty-state">
        <i class="fas fa-heart-broken"></i>
        <p>还没有收藏任何游戏</p>
        <button class="btn-primary" @click="goToHome">去首页逛逛</button>
      </div>

      <div v-else class="favorite-grid">
        <div v-for="game in favorites" :key="game.gameId" class="favorite-card">
          <div class="favorite-cover" @click="openGame(game)">
            <img v-if="game.imageUrl" :src="game.imageUrl" :alt="game.name" />
            <div v-else class="cover-placeholder"><i class="fas fa-gamepad"></i></div>
            <span v-if="game.vipOnly" class="vip-tag">VIP</span>
          </div>

          <div class="favorite-info">
            <h3>{{ game.name }}</h3>
            <p>{{ game.description || '暂无简介' }}</p>
            <div class="favorite-meta">
              <span><i class="fas fa-tag"></i> {{ game.category || '未分类' }}</span>
              <span><i class="fas fa-clock"></i> {{ formatTime(game.favoritedAt) }}</span>
            </div>
          </div>

          <div class="favorite-actions">
            <button class="btn-play" :disabled="!game.canAccess" @click="openGame(game)">
              <i :class="game.canAccess ? 'fas fa-play' : 'fas fa-lock'"></i>
              {{ game.canAccess ? '进入' : '需要 VIP' }}
            </button>
            <button class="btn-unfavorite" @click="removeFavorite(game)">
              <i class="fas fa-heart-broken"></i> 取消收藏
            </button>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import UserAvatarMenu from '../components/UserAvatarMenu.vue'
import { userApi } from '../api/user'
import { subscriptionApi } from '../api/subscription'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeTab = ref('password')
const changingPassword = ref(false)
const loadingFavorites = ref(false)
const loadingHistory = ref(false)
const loadingOrders = ref(false)
const passwordMessage = ref('')
const passwordSuccess = ref(false)
const favorites = ref([])
const browseHistory = ref([])
const libraryLimits = ref(null)
const orders = ref([])
const subscription = ref(null)
const payingOrderId = ref(null)

const planNameMap = {
  monthly: '月度会员',
  quarterly: '季度会员',
  yearly: '年度会员',
  lifetime: '终身会员'
}

const vipPlanLabel = computed(() => {
  if (!subscription.value) return '加载中...'
  if (subscription.value.isAdmin) return '管理员'
  if (subscription.value.vipActive) {
    const code = subscription.value.latestPlanCode
    if (code && planNameMap[code]) return planNameMap[code]
    if (subscription.value.vipExpiryTime === '永久') return '终身会员'
    return 'VIP 会员'
  }
  return '免费用户'
})

const vipExpiryLabel = computed(() => {
  if (!subscription.value) return ''
  if (subscription.value.isAdmin) return '拥有全部权限'
  if (!subscription.value.vipActive) return '尚未开通 VIP'
  return subscription.value.vipExpiryTime === '永久'
    ? '永久有效'
    : `有效期至 ${subscription.value.vipExpiryTime}`
})

const vipCardClass = computed(() => {
  if (!subscription.value) return ''
  if (subscription.value.isAdmin) return 'admin'
  if (subscription.value.vipActive) return 'active'
  return 'inactive'
})

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

onMounted(async () => {
  await userStore.checkLoginStatus()
  if (!userStore.isLoggedIn) {
    alert('请先登录')
    router.push('/home')
    return
  }

  const tab = route.query.tab
  if (tab === 'history' || tab === 'favorites' || tab === 'orders' || tab === 'password') {
    activeTab.value = tab
  }

  await Promise.all([loadSubscription(), loadLibraryLimits()])

  if (activeTab.value === 'favorites') {
    await loadFavorites()
  } else if (activeTab.value === 'history') {
    await loadBrowseHistory()
  } else if (activeTab.value === 'orders') {
    await loadOrders()
  } else {
    await loadFavorites()
  }
})

async function loadLibraryLimits() {
  try {
    const response = await userApi.getLibraryLimits()
    libraryLimits.value = response.data
  } catch (error) {
    console.error('加载容量限制失败:', error)
  }
}

function switchToFavorites() {
  activeTab.value = 'favorites'
  loadFavorites()
}

function switchToHistory() {
  activeTab.value = 'history'
  loadBrowseHistory()
}

function switchToOrders() {
  activeTab.value = 'orders'
  loadOrders()
}

async function loadSubscription() {
  try {
    const response = await subscriptionApi.getSubscription()
    if (response.data.success) {
      subscription.value = response.data
    }
  } catch (error) {
    console.error('加载订阅状态失败:', error)
  }
}

async function loadOrders() {
  loadingOrders.value = true
  try {
    const [subRes, ordersRes] = await Promise.all([
      subscriptionApi.getSubscription(),
      subscriptionApi.getOrders()
    ])
    if (subRes.data.success) {
      subscription.value = subRes.data
    }
    orders.value = ordersRes.data
    await userStore.checkLoginStatus()
  } catch (error) {
    console.error('加载订单失败:', error)
    if (error.response?.status === 401) {
      alert('请先登录')
      router.push('/home')
    }
  } finally {
    loadingOrders.value = false
  }
}

async function continuePay(order) {
  if (!confirm(`确认支付订单 #${formatOrderNo(order.id)}（${order.planName} ¥${formatPrice(order.amount)}）？`)) {
    return
  }

  payingOrderId.value = order.id
  try {
    await new Promise(resolve => setTimeout(resolve, 800))
    const confirmRes = await subscriptionApi.confirmPayment(order.id)
    if (!confirmRes.data.success) {
      throw new Error(confirmRes.data.message || '支付失败')
    }
    await userStore.checkLoginStatus()
    alert('支付成功，VIP 已激活')
    await loadOrders()
  } catch (error) {
    alert(error.response?.data?.message || error.message || '支付失败')
  } finally {
    payingOrderId.value = null
  }
}

async function submitPasswordChange() {
  passwordMessage.value = ''
  passwordSuccess.value = false

  if (!passwordForm.value.oldPassword) {
    passwordMessage.value = '请输入当前密码'
    return
  }
  if (passwordForm.value.newPassword.length < 6) {
    passwordMessage.value = '新密码长度不能少于 6 位'
    return
  }
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    passwordMessage.value = '两次输入的新密码不一致'
    return
  }

  changingPassword.value = true
  try {
    const response = await userApi.changePassword(
        passwordForm.value.oldPassword,
        passwordForm.value.newPassword
    )
    if (response.data.success) {
      passwordSuccess.value = true
      passwordMessage.value = '密码修改成功'
      passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
    } else {
      passwordMessage.value = response.data.message || '修改失败'
    }
  } catch (error) {
    passwordMessage.value = error.response?.data?.message || '修改失败，请稍后重试'
  } finally {
    changingPassword.value = false
  }
}

async function loadFavorites() {
  loadingFavorites.value = true
  try {
    const response = await userApi.getFavorites()
    favorites.value = response.data
    await loadLibraryLimits()
  } catch (error) {
    console.error('加载收藏失败:', error)
    if (error.response?.status === 401) {
      alert('请先登录')
      router.push('/home')
    }
  } finally {
    loadingFavorites.value = false
  }
}

async function loadBrowseHistory() {
  loadingHistory.value = true
  try {
    const response = await userApi.getBrowseHistory()
    browseHistory.value = response.data
    await loadLibraryLimits()
  } catch (error) {
    console.error('加载浏览记录失败:', error)
    if (error.response?.status === 401) {
      alert('请先登录')
      router.push('/home')
    }
  } finally {
    loadingHistory.value = false
  }
}

async function removeHistoryItem(item) {
  try {
    const response = await userApi.removeBrowseHistory(item.gameId)
    if (response.data.success) {
      browseHistory.value = browseHistory.value.filter(entry => entry.gameId !== item.gameId)
      await loadLibraryLimits()
    }
  } catch (error) {
    alert('删除失败')
  }
}

async function clearBrowseHistory() {
  if (!confirm('确定清空全部浏览记录？')) return
  try {
    const response = await userApi.clearBrowseHistory()
    if (response.data.success) {
      browseHistory.value = []
      await loadLibraryLimits()
    }
  } catch (error) {
    alert('清空失败')
  }
}

function openHistoryGame(item) {
  if (!item.canAccess) {
    if (confirm('该游戏需要 VIP 权限，是否前往开通？')) {
      router.push('/service')
    }
    return
  }
  openGame(item)
}

function openGame(game) {
  if (!game.canAccess) {
    if (confirm('该游戏需要 VIP 权限，是否前往开通？')) {
      router.push('/service')
    }
    return
  }
  if (game.link && game.link !== '#') {
    window.open(game.link, '_blank')
  }
}

async function removeFavorite(game) {
  if (!confirm(`确定取消收藏「${game.name}」？`)) return

  try {
    const response = await userApi.removeFavorite(game.gameId)
    if (response.data.success) {
      favorites.value = favorites.value.filter(item => item.gameId !== game.gameId)
    } else {
      alert(response.data.message || '操作失败')
    }
  } catch (error) {
    alert('操作失败')
  }
}

function goToHome() {
  router.push('/home')
}

function formatTime(value) {
  if (!value) return '-'
  return new Date(value).toLocaleString('zh-CN')
}

function formatPrice(value) {
  if (value == null) return '0'
  const num = Number(value)
  return Number.isInteger(num) ? String(num) : num.toFixed(2)
}

function formatOrderNo(id) {
  return String(id).padStart(6, '0')
}

function formatDateTime(value) {
  if (!value) return '-'
  return String(value).replace('T', ' ').slice(0, 16)
}

function formatExpiry(value) {
  if (value == null) return '永久'
  return formatDateTime(value).slice(0, 10)
}

function statusLabel(status) {
  const map = {
    PAID: '已支付',
    PENDING: '待支付',
    FAILED: '支付失败',
    CANCELLED: '已取消'
  }
  return map[status] || status
}
</script>

<style scoped>
.user-center-container {
  max-width: 1100px;
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
  gap: 20px;
  align-items: center;
}

.nav-links a {
  color: #cbd5e1;
  text-decoration: none;
}

.nav-links a:hover,
.nav-links a.active {
  color: #a78bfa;
}

.buy-now {
  background: linear-gradient(145deg, #7c3aed, #a78bfa);
  padding: 8px 18px;
  border-radius: 40px;
  color: white !important;
  font-weight: 600;
}

.avatar-btn {
  background: #161e2a;
  border: 1px solid #2a3748;
  padding: 8px 16px;
  border-radius: 8px;
  color: #edf2f7;
  display: flex;
  align-items: center;
  gap: 8px;
}

.page-hero { margin-bottom: 28px; }
.page-title { font-size: 2rem; font-weight: 700; margin-bottom: 8px; }
.page-subtitle { color: #94a3b8; }

.vip-status-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 18px 24px;
  margin-bottom: 24px;
  border-radius: 14px;
  border: 1px solid #2a3748;
  background: #161e2a;
}

.vip-status-card.active {
  border-color: #7c3aed50;
  background: linear-gradient(135deg, #161e2a, #1a103550);
}

.vip-status-card.inactive {
  border-color: #334155;
}

.vip-status-card.admin {
  border-color: #f472b650;
  background: linear-gradient(135deg, #161e2a, #2a152550);
}

.vip-status-main {
  display: flex;
  align-items: center;
  gap: 14px;
}

.vip-status-main i {
  font-size: 1.6rem;
  color: #a78bfa;
}

.vip-label {
  display: block;
  font-weight: 700;
  font-size: 1.1rem;
}

.vip-expiry {
  display: block;
  color: #94a3b8;
  font-size: 0.9rem;
  margin-top: 4px;
}

.vip-status-meta {
  color: #cbd5e1;
  font-size: 0.95rem;
  text-align: right;
}

.renew-link {
  color: #a78bfa;
  text-decoration: none;
  font-weight: 600;
}

.renew-link:hover { text-decoration: underline; }

.order-table-wrap {
  overflow-x: auto;
}

.order-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.9rem;
}

.order-table th,
.order-table td {
  padding: 12px 14px;
  text-align: left;
  border-bottom: 1px solid #2a3748;
}

.order-table th {
  color: #94a3b8;
  font-weight: 600;
}

.order-no {
  color: #a78bfa;
  background: #7c3aed20;
  padding: 2px 8px;
  border-radius: 6px;
}

.plan-name { display: block; font-weight: 600; }
.plan-code { display: block; color: #64748b; font-size: 0.8rem; }
.amount { color: #f472b6; font-weight: 600; }

.badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 0.8rem;
}

.badge.paid { background: #34d39920; color: #34d399; }
.badge.pending { background: #fbbf2420; color: #fbbf24; }

.btn-pay {
  padding: 6px 12px;
  background: #7c3aed;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.85rem;
}

.btn-pay:disabled { opacity: 0.6; cursor: not-allowed; }

.order-action { white-space: nowrap; }

.tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.tab-btn {
  padding: 10px 20px;
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 10px;
  color: #94a3b8;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.tab-btn.active {
  background: #7c3aed30;
  border-color: #7c3aed;
  color: #a78bfa;
}

.tab-badge {
  background: #f472b6;
  color: white;
  font-size: 0.75rem;
  padding: 2px 8px;
  border-radius: 10px;
}

.panel {
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 16px;
  padding: 28px;
}

.panel h2 {
  font-size: 1.3rem;
  margin-bottom: 8px;
}

.panel-desc {
  color: #94a3b8;
  margin-bottom: 24px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.panel-actions {
  display: flex;
  gap: 8px;
}

.btn-secondary.danger:hover {
  color: #f87171;
  border-color: #f87171;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.history-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #0f172a;
  border: 1px solid #2a3748;
  border-radius: 12px;
}

.history-main {
  flex: 1;
  cursor: pointer;
}

.history-title {
  font-weight: 600;
  margin-bottom: 6px;
}

.vip-tag-inline {
  margin-left: 8px;
  font-size: 0.75rem;
  color: #f472b6;
}

.history-meta {
  display: flex;
  gap: 16px;
  color: #64748b;
  font-size: 0.85rem;
}

.history-actions {
  display: flex;
  gap: 8px;
}

.btn-play.small,
.btn-unfavorite.small {
  width: 36px;
  height: 36px;
  padding: 0;
  justify-content: center;
}

.btn-play:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.password-form {
  max-width: 480px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  color: #94a3b8;
  font-size: 0.9rem;
}

.form-group input {
  width: 100%;
  padding: 12px 14px;
  background: #0f172a;
  border: 1px solid #2a3748;
  border-radius: 8px;
  color: #edf2f7;
  box-sizing: border-box;
}

.form-message {
  margin: 12px 0;
  font-size: 0.9rem;
}

.form-message.success { color: #34d399; }
.form-message.error { color: #f87171; }

.btn-primary,
.btn-secondary {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.btn-primary {
  background: linear-gradient(135deg, #7c3aed, #a78bfa);
  color: white;
}

.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }

.btn-secondary {
  background: #2a3748;
  color: #cbd5e1;
}

.loading-state,
.empty-state {
  text-align: center;
  padding: 48px 20px;
  color: #94a3b8;
}

.loading-state i,
.empty-state i {
  font-size: 2.5rem;
  margin-bottom: 12px;
  display: block;
}

.favorite-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.favorite-card {
  background: #0f172a;
  border: 1px solid #2a3748;
  border-radius: 12px;
  overflow: hidden;
}

.favorite-cover {
  position: relative;
  height: 140px;
  background: #1e2a3a;
  cursor: pointer;
}

.favorite-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2.5rem;
  color: #64748b;
}

.vip-tag {
  position: absolute;
  top: 10px;
  right: 10px;
  background: #f472b6;
  color: white;
  font-size: 0.75rem;
  padding: 4px 8px;
  border-radius: 6px;
}

.favorite-info {
  padding: 16px;
}

.favorite-info h3 {
  margin-bottom: 8px;
}

.favorite-info p {
  color: #94a3b8;
  font-size: 0.9rem;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.favorite-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 0.85rem;
  color: #64748b;
}

.favorite-actions {
  display: flex;
  gap: 8px;
  padding: 0 16px 16px;
}

.btn-play,
.btn-unfavorite {
  flex: 1;
  padding: 8px 12px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.9rem;
}

.btn-play {
  background: #7c3aed;
  color: white;
}

.btn-unfavorite {
  background: #2a3748;
  color: #cbd5e1;
}

@media (max-width: 768px) {
  .header { flex-direction: column; gap: 16px; }
  .tabs { flex-direction: column; }
}
</style>
