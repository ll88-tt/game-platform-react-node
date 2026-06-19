<template>
  <div class="subscription-container">
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
        <router-link to="/service" class="buy-now">
          订阅VIP <i class="fas fa-arrow-right"></i>
        </router-link>
      </nav>

      <UserAvatarMenu @login="goToHome" />
    </header>

    <section class="page-hero">
      <h1 class="page-title"><i class="fas fa-crown"></i> 我的订阅</h1>
      <p class="page-subtitle">查看 VIP 状态与历史订单</p>
    </section>

    <div v-if="loading" class="loading-state">
      <i class="fas fa-spinner fa-spin"></i>
      <p>加载中...</p>
    </div>

    <template v-else>
      <section class="status-section">
        <div class="status-card">
          <div class="status-header">
            <h2><i class="fas fa-id-card"></i> 当前会员状态</h2>
            <span class="status-badge" :class="subscription?.vipActive ? 'active' : 'inactive'">
              {{ subscription?.vipActive ? '已激活' : '未激活' }}
            </span>
          </div>

          <div class="status-grid">
            <div class="status-item">
              <span class="label">用户名</span>
              <span class="value">{{ subscription?.username || userStore.currentUser }}</span>
            </div>
            <div class="status-item">
              <span class="label">会员等级</span>
              <span class="value" :class="planClass">{{ planName }}</span>
            </div>
            <div class="status-item">
              <span class="label">VIP 状态</span>
              <span class="value">{{ subscription?.vipStatus || '非VIP用户' }}</span>
            </div>
            <div class="status-item">
              <span class="label">开通时间</span>
              <span class="value">{{ subscription?.vipStartTime || '-' }}</span>
            </div>
            <div class="status-item">
              <span class="label">有效期至</span>
              <span class="value highlight">{{ expiryDisplay }}</span>
            </div>
            <div class="status-item">
              <span class="label">剩余天数</span>
              <span class="value">{{ daysRemainingDisplay }}</span>
            </div>
          </div>

          <div class="action-row">
            <button class="btn-primary" @click="goToRenew">
              <i class="fas fa-sync-alt"></i>
              {{ subscription?.vipActive ? '续费 / 升级' : '立即订阅' }}
            </button>
            <button class="btn-secondary" @click="loadData">
              <i class="fas fa-redo"></i> 刷新
            </button>
          </div>
        </div>
      </section>

      <section class="orders-section">
        <div class="section-header">
          <h2><i class="fas fa-receipt"></i> 历史订单</h2>
          <span class="order-count">共 {{ orders.length }} 笔</span>
        </div>

        <div v-if="orders.length === 0" class="empty-state">
          <i class="fas fa-shopping-bag"></i>
          <p>暂无订单记录</p>
          <button class="btn-primary" @click="goToRenew">去订阅 VIP</button>
        </div>

        <div v-else class="table-wrapper">
          <table class="orders-table">
            <thead>
              <tr>
                <th>订单号</th>
                <th>套餐</th>
                <th>金额</th>
                <th>状态</th>
                <th>购买时间</th>
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
                <td><span class="badge" :class="order.status === 'PAID' ? 'paid' : 'pending'">{{ statusLabel(order.status) }}</span></td>
                <td>{{ formatDateTime(order.createdAt) }}</td>
                <td>{{ formatExpiry(order.vipExpiryAfter) }}</td>
                <td v-if="order.status === 'PENDING'" class="order-action">
                  <button class="btn-pay" :disabled="payingOrderId === order.id" @click="continuePay(order)">
                    <i v-if="payingOrderId === order.id" class="fas fa-spinner fa-spin"></i>
                    <i v-else class="fas fa-wallet"></i>
                    继续支付
                  </button>
                </td>
                <td v-else class="order-action">-</td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import UserAvatarMenu from '../components/UserAvatarMenu.vue'
import { subscriptionApi } from '../api/subscription'

const router = useRouter()
const userStore = useUserStore()

const showDropdown = ref(false)
const loading = ref(true)
const subscription = ref(null)
const orders = ref([])
const payingOrderId = ref(null)

const planNameMap = {
  monthly: '月度会员',
  quarterly: '季度会员',
  yearly: '年度会员',
  lifetime: '终身会员',
  FREE: '免费用户'
}

const planName = computed(() => {
  if (subscription.value?.isAdmin) return '管理员'
  const code = subscription.value?.latestPlanCode
  if (code && planNameMap[code]) return planNameMap[code]
  if (subscription.value?.vipActive && subscription.value?.vipExpiryTime === '永久') return '终身会员'
  if (subscription.value?.vipActive) return 'VIP 会员'
  return '免费用户'
})

const planClass = computed(() => {
  const code = subscription.value?.latestPlanCode
  if (subscription.value?.isAdmin) return 'lifetime'
  return code || (subscription.value?.vipActive ? 'monthly' : 'free')
})

const expiryDisplay = computed(() => {
  if (!subscription.value?.vipActive) return '-'
  return subscription.value?.vipExpiryTime || '永久'
})

const daysRemainingDisplay = computed(() => {
  if (!subscription.value?.vipActive) return '-'
  const days = subscription.value?.daysRemaining
  if (days == null) return '永久'
  if (days <= 0) return '已到期'
  return `${days} 天`
})

onMounted(async () => {
  await userStore.checkLoginStatus()
  if (!userStore.isLoggedIn) {
    alert('请先登录')
    router.push('/home')
    return
  }
  await loadData()
})

async function loadData() {
  loading.value = true
  try {
    const [subRes, ordersRes] = await Promise.all([
      subscriptionApi.getSubscription(),
      subscriptionApi.getOrders()
    ])
    subscription.value = subRes.data
    orders.value = ordersRes.data
    await userStore.checkLoginStatus()
  } catch (error) {
    console.error('加载订阅信息失败:', error)
    if (error.response?.status === 401) {
      alert('请先登录')
      router.push('/home')
    } else {
      alert('加载失败，请稍后重试')
    }
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
  showDropdown.value = false
  router.push('/home')
}

function goToSubscription() {
  showDropdown.value = false
}

function goToRenew() {
  router.push('/service')
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
    await loadData()
  } catch (error) {
    console.error('继续支付失败:', error)
    alert(error.response?.data?.message || error.message || '支付失败')
  } finally {
    payingOrderId.value = null
  }
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
.subscription-container {
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
  gap: 24px;
  align-items: center;
}

.nav-links a {
  color: #cbd5e1;
  text-decoration: none;
}

.nav-links a:hover { color: #a78bfa; }

.buy-now {
  background: linear-gradient(145deg, #7c3aed, #a78bfa);
  padding: 8px 18px;
  border-radius: 40px;
  color: white !important;
  font-weight: 600;
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
  min-width: 180px;
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

.page-hero {
  text-align: center;
  margin-bottom: 40px;
}

.page-title {
  font-size: 2.4rem;
  font-weight: 700;
  margin-bottom: 8px;
}

.page-subtitle { color: #94a3b8; }

.loading-state,
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #94a3b8;
}

.loading-state i,
.empty-state i {
  font-size: 2.5rem;
  color: #a78bfa;
  margin-bottom: 12px;
  display: block;
}

.status-section { margin-bottom: 40px; }

.status-card {
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 16px;
  overflow: hidden;
}

.status-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 32px;
  background: linear-gradient(135deg, #7c3aed20, #a78bfa20);
  border-bottom: 1px solid #2a3748;
}

.status-header h2 {
  font-size: 1.4rem;
  display: flex;
  align-items: center;
  gap: 10px;
}

.status-badge {
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
}

.status-badge.active { background: #10b98130; color: #34d399; }
.status-badge.inactive { background: #64748b30; color: #94a3b8; }

.status-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 24px;
  padding: 32px;
}

.status-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.label { color: #94a3b8; font-size: 0.9rem; }
.value { font-size: 1.1rem; font-weight: 600; }
.value.highlight { color: #a78bfa; }
.value.monthly { color: #34d399; }
.value.quarterly { color: #fbbf24; }
.value.yearly { color: #a78bfa; }
.value.lifetime { color: #f472b6; }
.value.free { color: #94a3b8; }

.action-row {
  display: flex;
  gap: 12px;
  padding: 0 32px 32px;
  flex-wrap: wrap;
}

.btn-primary,
.btn-secondary {
  padding: 12px 24px;
  border: none;
  border-radius: 10px;
  font-weight: 600;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.btn-primary {
  background: linear-gradient(135deg, #7c3aed, #a78bfa);
  color: white;
}

.btn-secondary {
  background: #2a3748;
  color: #cbd5e1;
}

.orders-section {
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 16px;
  overflow: hidden;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 32px;
  border-bottom: 1px solid #2a3748;
}

.section-header h2 {
  font-size: 1.3rem;
  display: flex;
  align-items: center;
  gap: 10px;
}

.order-count { color: #94a3b8; font-size: 0.9rem; }

.table-wrapper { overflow-x: auto; }

.orders-table {
  width: 100%;
  border-collapse: collapse;
}

.orders-table th,
.orders-table td {
  padding: 14px 20px;
  text-align: left;
  border-bottom: 1px solid #2a3748;
}

.orders-table th {
  background: #1e2a3a;
  color: #94a3b8;
  font-size: 0.85rem;
}

.order-no {
  background: #0f172a;
  padding: 2px 8px;
  border-radius: 4px;
  color: #a78bfa;
  font-size: 0.85rem;
}

.plan-name { display: block; font-weight: 600; }
.plan-code { font-size: 0.8rem; color: #64748b; }
.amount { color: #34d399; font-weight: 600; }

.badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
}

.badge.paid { background: #10b98130; color: #34d399; }
.badge.pending { background: #f59e0b30; color: #fbbf24; }

.btn-pay {
  padding: 6px 12px;
  background: linear-gradient(135deg, #7c3aed, #a78bfa);
  border: none;
  border-radius: 8px;
  color: white;
  font-size: 0.85rem;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.btn-pay:disabled { opacity: 0.6; cursor: not-allowed; }
.order-action { white-space: nowrap; }

.empty-state .btn-primary { margin-top: 16px; }

@media (max-width: 768px) {
  .header { flex-direction: column; gap: 16px; }
  .action-row { flex-direction: column; }
}
</style>
