<template>
  <div class="service-container">
    <!-- 导航栏 -->
    <header class="header">
      <div class="logo">
        <i class="fas fa-gamepad" style="margin-right: 6px; color: #c084fc;"></i>
        GAMEVILA
      </div>

      <nav class="nav-links">
        <router-link to="/home">首页</router-link>
        <router-link to="/about">关于</router-link>
        <router-link to="/service" class="active">服务</router-link>
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

      <UserAvatarMenu
          :show-admin-dashboard="userStore.isAdmin"
          :show-admin-games="userStore.isAdmin"
          :show-admin-feedback="userStore.isAdmin"
          show-browse-history
          @login="openLoginModal"
          @browse-history="openHistoryPanel"
      />
    </header>

    <!-- 页面标题 -->
    <section class="page-hero">
      <h1 class="page-title">
        <i class="fas fa-crown"></i> VIP 订阅服务
      </h1>
      <p class="page-subtitle">解锁全部游戏，享受专属特权</p>
    </section>

    <VipExpiryBanner :dismissible="true" />

    <!-- 当前会员状态 -->
    <section v-if="userStore.isLoggedIn" class="current-status">
      <div class="status-card">
        <div class="status-header">
          <h2><i class="fas fa-id-card"></i> 当前会员状态</h2>
        </div>

        <div class="status-body">
          <div class="status-item">
            <span class="status-label">用户名</span>
            <span class="status-value">{{ userStore.currentUser }}</span>
          </div>

          <div class="status-item">
            <span class="status-label">会员等级</span>
            <span class="status-value" :class="userStore.permissionLevel.toLowerCase()">
              {{ permissionName }}
            </span>
          </div>

          <div class="status-item">
            <span class="status-label">VIP 状态</span>
            <span class="status-value" :class="userStore.isVip ? 'active' : 'inactive'">
              {{ userStore.isVip ? '已激活' : '未激活' }}
            </span>
          </div>

          <div v-if="userStore.vipExpiryTime" class="status-item">
            <span class="status-label">有效期至</span>
            <span class="status-value">{{ userStore.vipExpiryTime }}</span>
          </div>

          <div
              v-if="userStore.vipActive && userStore.vipDaysRemaining != null && !userStore.isVipLifetime"
              class="status-item"
          >
            <span class="status-label">剩余天数</span>
            <span class="status-value" :class="userStore.vipDaysRemaining <= 10 ? 'warning' : 'active'">
              {{ userStore.vipDaysRemaining === 0 ? '今日到期' : `${userStore.vipDaysRemaining} 天` }}
            </span>
          </div>

          <div v-if="userStore.isAdmin" class="status-item">
            <span class="status-label">管理员权限</span>
            <span class="status-value active">是</span>
          </div>
        </div>
      </div>
    </section>

    <!-- VIP 套餐 -->
    <section class="pricing-section">
      <div class="section-header">
        <h2><i class="fas fa-tags"></i> 选择您的订阅方案</h2>
        <p>灵活多样的会员计划，满足不同需求</p>
      </div>

      <div v-if="plansLoading" class="pricing-loading">
        <i class="fas fa-spinner fa-spin"></i>
        <p>加载套餐中...</p>
      </div>

      <div v-else-if="membershipPlans.length === 0" class="pricing-empty">
        <p>暂无可用的会员套餐</p>
      </div>

      <div v-else class="pricing-grid">
        <div
            v-for="plan in membershipPlans"
            :key="plan.id"
            class="pricing-card"
            :class="{
              recommended: plan.recommended,
              'lifetime-card': plan.planCode === 'lifetime'
            }"
        >
          <div v-if="plan.recommended" class="recommended-badge">
            <i class="fas fa-star"></i> 最受欢迎
          </div>

          <div class="card-header" :class="plan.planCode">
            <i :class="planIcons[plan.planCode]"></i>
            <h3>{{ plan.name }}</h3>
          </div>

          <div class="card-body">
            <div class="price">
              <span v-if="plan.originalPrice" class="original-price-inline">¥{{ formatPrice(plan.originalPrice) }}</span>
              <span class="currency">¥</span>
              <span class="amount">{{ formatPrice(plan.price) }}</span>
              <span class="period">{{ plan.periodLabel || '' }}</span>
            </div>

            <div v-if="getSavingsText(plan)" class="savings">
              {{ getSavingsText(plan) }}
            </div>

            <ul class="features">
              <li
                  v-for="(feature, index) in planFeatures[plan.planCode] || []"
                  :key="index"
                  :class="{ disabled: feature.disabled }"
              >
                <i :class="feature.disabled ? 'fas fa-times' : 'fas fa-check'"></i>
                {{ feature.text }}
              </li>
            </ul>

            <button
                class="subscribe-btn"
                :class="plan.planCode"
                @click.stop="handleSubscribe(plan)"
            >
              立即订阅
            </button>
          </div>
        </div>
      </div>
    </section>

    <!-- VIP 特权说明 -->
    <section class="benefits-section">
      <div class="section-header">
        <h2><i class="fas fa-gift"></i> VIP 专属特权</h2>
      </div>

      <div class="benefits-grid">
        <div class="benefit-card">
          <div class="benefit-icon"><i class="fas fa-gamepad"></i></div>
          <h3>海量游戏库</h3>
          <p>解锁 100+ 精选独立游戏，涵盖冒险、RPG、竞速等多种类型</p>
        </div>

        <div class="benefit-card">
          <div class="benefit-icon"><i class="fas fa-ban"></i></div>
          <h3>无广告体验</h3>
          <p>完全去除所有广告干扰，沉浸式游戏体验</p>
        </div>

        <div class="benefit-card">
          <div class="benefit-icon"><i class="fas fa-cloud"></i></div>
          <h3>云存档同步</h3>
          <p>游戏进度自动云端保存，多设备无缝切换</p>
        </div>

        <div class="benefit-card">
          <div class="benefit-icon"><i class="fas fa-headset"></i></div>
          <h3>专属客服</h3>
          <p>VIP 用户享有优先客服支持，快速响应您的问题</p>
        </div>

        <div class="benefit-card">
          <div class="benefit-icon"><i class="fas fa-percentage"></i></div>
          <h3>专属折扣</h3>
          <p>购买游戏享受额外折扣，最高可达 7 折优惠</p>
        </div>

        <div class="benefit-card">
          <div class="benefit-icon"><i class="fas fa-rocket"></i></div>
          <h3>抢先体验</h3>
          <p>新功能、新游戏优先体验权，快人一步</p>
        </div>
      </div>
    </section>

    <!-- 常见问题 -->
    <section class="faq-section">
      <div class="section-header">
        <h2><i class="fas fa-question-circle"></i> 常见问题</h2>
      </div>

      <div class="faq-list">
        <div class="faq-item">
          <div class="faq-question" @click="toggleFaq(0)">
            <h3>如何成为 VIP 会员？</h3>
            <i class="fas fa-chevron-down" :class="{ active: openFaq === 0 }"></i>
          </div>
          <div v-show="openFaq === 0" class="faq-answer">
            <p>点击上方任意套餐的"立即订阅"按钮，完成支付后即可成为 VIP 会员。我们支持支付宝、微信支付等多种支付方式。</p>
          </div>
        </div>

        <div class="faq-item">
          <div class="faq-question" @click="toggleFaq(1)">
            <h3>VIP 会员可以退款吗？</h3>
            <i class="fas fa-chevron-down" :class="{ active: openFaq === 1 }"></i>
          </div>
          <div v-show="openFaq === 1" class="faq-answer">
            <p>订阅后 7 天内可以申请全额退款。超过 7 天后，将按照实际使用天数扣除费用后退还剩余金额。</p>
          </div>
        </div>

        <div class="faq-item">
          <div class="faq-question" @click="toggleFaq(2)">
            <h3>会员到期后会怎样？</h3>
            <i class="fas fa-chevron-down" :class="{ active: openFaq === 2 }"></i>
          </div>
          <div v-show="openFaq === 2" class="faq-answer">
            <p>会员到期后，您将无法继续访问 VIP 专属游戏，但可以继续使用免费游戏。您可以随时续费恢复 VIP 权限。</p>
          </div>
        </div>

        <div class="faq-item">
          <div class="faq-question" @click="toggleFaq(3)">
            <h3>可以在多个设备上使用吗？</h3>
            <i class="fas fa-chevron-down" :class="{ active: openFaq === 3 }"></i>
          </div>
          <div v-show="openFaq === 3" class="faq-answer">
            <p>是的！您的 VIP 会员资格与账号绑定，可以在任何设备上登录使用，游戏进度会自动同步。</p>
          </div>
        </div>

        <div class="faq-item">
          <div class="faq-question" @click="toggleFaq(4)">
            <h3>如何取消自动续费？</h3>
            <i class="fas fa-chevron-down" :class="{ active: openFaq === 4 }"></i>
          </div>
          <div v-show="openFaq === 4" class="faq-answer">
            <p>目前我们采用手动续费模式，不会自动扣费。会员到期后如需继续使用，请手动续费。</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 登录提示弹窗 -->
    <div v-if="showLoginPrompt" class="modal-overlay" @click="closeLoginPrompt">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h2><i class="fas fa-lock"></i> 需要登录</h2>
          <button class="close-btn" @click="closeLoginPrompt">
            <i class="fas fa-times"></i>
          </button>
        </div>

        <div class="modal-body">
          <p>请先登录后再订阅 VIP 服务</p>
        </div>

        <div class="modal-footer">
          <button class="btn-secondary" @click="closeLoginPrompt">取消</button>
          <button class="btn-primary" @click="goToLogin">去登录</button>
        </div>
      </div>
    </div>

    <!-- 模拟支付弹窗 -->
    <div v-if="showPaymentModal" class="modal-overlay" @click.self="closePaymentModal">
      <div class="modal payment-modal" @click.stop>
        <div class="modal-header">
          <h2><i class="fas fa-credit-card"></i> 确认支付</h2>
          <button class="close-btn" :disabled="paying" @click="closePaymentModal">
            <i class="fas fa-times"></i>
          </button>
        </div>

        <div v-if="selectedPlan" class="modal-body payment-body">
            <div v-if="paymentStep === 'confirm'" class="payment-summary">
            <div class="payment-plan-name">{{ selectedPlan.name }}</div>
            <div class="payment-amount">
              <span class="currency">¥</span>
              <span class="amount">{{ formatPrice(selectedPlan.price) }}</span>
              <span class="period">{{ selectedPlan.periodLabel || '' }}</span>
            </div>
            <p v-if="getSavingsText(selectedPlan)" class="payment-savings">{{ getSavingsText(selectedPlan) }}</p>
            <p v-if="pendingOrder?.orderId" class="payment-order-id">
              订单号：#{{ formatOrderNo(pendingOrder.orderId) }}
              <span v-if="creatingOrder" class="creating-tag">创建中...</span>
            </p>
            <p v-if="paymentError" class="payment-error">{{ paymentError }}</p>
            <p class="payment-hint">演示环境：点击下方按钮将模拟支付成功并自动开通 VIP</p>
          </div>

          <div v-else-if="paymentStep === 'processing'" class="payment-processing">
            <i class="fas fa-spinner fa-spin"></i>
            <p>正在处理支付...</p>
            <small>模拟支付网关回调中</small>
          </div>

          <div v-else class="payment-success">
            <i class="fas fa-check-circle"></i>
            <p>支付成功！</p>
            <small>VIP 已激活，即将跳转至我的订阅</small>
          </div>
        </div>

        <div v-if="paymentStep === 'confirm'" class="modal-footer">
          <button class="btn-secondary" :disabled="paying || creatingOrder" @click="closePaymentModal">取消</button>
          <button class="btn-primary" :disabled="paying || creatingOrder || !pendingOrder?.orderId" @click="startPayment">
            <i class="fas fa-wallet"></i> {{ creatingOrder ? '创建订单中...' : '模拟支付' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { membershipPlansApi } from '../api/membershipPlans'
import { subscriptionApi } from '../api/subscription'
import VipExpiryBanner from '../components/VipExpiryBanner.vue'
import UserAvatarMenu from '../components/UserAvatarMenu.vue'
import { showVipReminderOnce } from '../utils/vipReminder'

const router = useRouter()
const userStore = useUserStore()

const showDropdown = ref(false)
const showLoginPrompt = ref(false)
const showPaymentModal = ref(false)
const openFaq = ref(null)
const selectedPlan = ref(null)
const membershipPlans = ref([])
const plansLoading = ref(false)
const paying = ref(false)
const creatingOrder = ref(false)
const paymentStep = ref('confirm')
const paymentError = ref('')
const pendingOrder = ref(null)

const planIcons = {
  monthly: 'fas fa-calendar-alt',
  quarterly: 'fas fa-gem',
  yearly: 'fas fa-trophy',
  lifetime: 'fas fa-crown'
}

const planFeatures = {
  monthly: [
    { text: '解锁全部 VIP 游戏' },
    { text: '无广告体验' },
    { text: '优先客服支持' },
    { text: '游戏存档云同步' },
    { text: '专属游戏折扣', disabled: true },
    { text: '新功能抢先体验', disabled: true }
  ],
  quarterly: [
    { text: '解锁全部 VIP 游戏' },
    { text: '无广告体验' },
    { text: '优先客服支持' },
    { text: '游戏存档云同步' },
    { text: '专属游戏折扣（9折）' },
    { text: '新功能抢先体验', disabled: true }
  ],
  yearly: [
    { text: '解锁全部 VIP 游戏' },
    { text: '无广告体验' },
    { text: '优先客服支持' },
    { text: '游戏存档云同步' },
    { text: '专属游戏折扣（8折）' },
    { text: '新功能抢先体验' }
  ],
  lifetime: [
    { text: '解锁全部 VIP 游戏' },
    { text: '无广告体验' },
    { text: '终身优先客服支持' },
    { text: '游戏存档云同步' },
    { text: '专属游戏折扣（7折）' },
    { text: '新功能永久抢先体验' },
    { text: '专属会员标识' },
    { text: '生日特别礼包' }
  ]
}

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
  await loadMembershipPlans()
  if (userStore.isLoggedIn) {
    showVipReminderOnce(userStore, 'service')
  }
})

async function loadMembershipPlans() {
  plansLoading.value = true
  try {
    const response = await membershipPlansApi.getEnabledPlans()
    membershipPlans.value = response.data
  } catch (error) {
    console.error('加载会员套餐失败:', error)
  } finally {
    plansLoading.value = false
  }
}

function formatPrice(value) {
  if (value == null) return '0'
  const num = Number(value)
  return Number.isInteger(num) ? String(num) : num.toFixed(2)
}

function toggleDropdown() {
  showDropdown.value = !showDropdown.value
}

function openLoginModal() {
  showDropdown.value = false
  router.push('/home')
}

async function handleLogout() {
  await userStore.logout()
  showDropdown.value = false
  //alert('已退出登录')
}

function goToAdminPanel() {
  showDropdown.value = false
  router.push('/admin-dashboard')
}

function goToSubscription() {
  showDropdown.value = false
  router.push('/subscription')
}

function openHistoryPanel() {
  showDropdown.value = false
  if (userStore.isLoggedIn) {
    router.push({ path: '/user-center', query: { tab: 'history' } })
  } else {
    router.push({ path: '/home', query: { history: '1' } })
  }
}

function getSavingsText(plan) {
  return plan.computedSavingsText || plan.displaySavingsText || plan.savingsText || ''
}

function formatOrderNo(id) {
  return String(id).padStart(6, '0')
}

async function handleSubscribe(plan) {
  if (!userStore.isLoggedIn) {
    selectedPlan.value = plan
    showLoginPrompt.value = true
    return
  }

  selectedPlan.value = plan
  pendingOrder.value = null
  paymentStep.value = 'confirm'
  paymentError.value = ''
  showPaymentModal.value = true

  await nextTick()
  creatingOrder.value = true

  try {
    const createRes = await subscriptionApi.createPayment(plan.planCode)
    if (!createRes.data.success) {
      throw new Error(createRes.data.message || '创建订单失败')
    }
    pendingOrder.value = createRes.data
  } catch (error) {
    console.error('创建订单失败:', error)
    const status = error.response?.status
    if (status === 403) {
      paymentError.value = '无权创建订单，请刷新页面后重新登录再试'
    } else if (status === 401) {
      paymentError.value = '登录已过期，请重新登录'
      await userStore.checkLoginStatus()
    } else {
      paymentError.value = error.response?.data?.message || error.message || '创建订单失败，请稍后重试'
    }
  } finally {
    creatingOrder.value = false
  }
}

function closePaymentModal() {
  if (paying.value || creatingOrder.value) return
  showPaymentModal.value = false
  selectedPlan.value = null
  pendingOrder.value = null
  paymentStep.value = 'confirm'
  paymentError.value = ''
}

async function startPayment() {
  if (!selectedPlan.value || paying.value || !pendingOrder.value?.orderId) return

  paying.value = true
  paymentStep.value = 'processing'

  try {
    await new Promise(resolve => setTimeout(resolve, 1200))

    const confirmRes = await subscriptionApi.confirmPayment(pendingOrder.value.orderId)
    if (!confirmRes.data.success) {
      throw new Error(confirmRes.data.message || '支付失败')
    }

    await userStore.checkLoginStatus()
    paymentStep.value = 'success'

    setTimeout(() => {
      closePaymentModal()
      router.push('/subscription')
    }, 1500)
  } catch (error) {
    console.error('支付失败:', error)
    paymentError.value = error.response?.data?.message || error.message || '支付失败，请重试'
    paymentStep.value = 'confirm'
  } finally {
    paying.value = false
  }
}

function closeLoginPrompt() {
  showLoginPrompt.value = false
  selectedPlan.value = null
}

function goToLogin() {
  closeLoginPrompt()
  router.push('/home')
}

function toggleFaq(index) {
  openFaq.value = openFaq.value === index ? null : index
}
</script>

<style scoped>
.service-container {
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

/* 当前会员状态 */
.current-status {
  margin-bottom: 64px;
}

.status-card {
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 16px;
  overflow: hidden;
}

.status-header {
  padding: 24px 32px;
  background: linear-gradient(135deg, #7c3aed20, #a78bfa20);
  border-bottom: 1px solid #2a3748;
}

.status-header h2 {
  font-size: 1.5rem;
  display: flex;
  align-items: center;
  gap: 12px;
}

.status-body {
  padding: 32px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 24px;
}

.status-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.status-label {
  color: #94a3b8;
  font-size: 0.9rem;
}

.status-value {
  font-size: 1.2rem;
  font-weight: 600;
  color: #edf2f7;
}

.status-value.lifetime {
  color: #f472b6;
}

.status-value.yearly {
  color: #a78bfa;
}

.status-value.quarterly {
  color: #fbbf24;
}

.status-value.monthly {
  color: #34d399;
}

.status-value.free {
  color: #94a3b8;
}

.status-value.active {
  color: #10b981;
}

.status-value.warning {
  color: #fbbf24;
}

.status-value.inactive {
  color: #ef4444;
}

/* 定价部分 */
.pricing-section {
  margin-bottom: 64px;
}

.pricing-loading,
.pricing-empty {
  text-align: center;
  padding: 48px;
  color: #94a3b8;
}

.pricing-loading i {
  font-size: 2rem;
  color: #a78bfa;
  margin-bottom: 12px;
  display: block;
}

.section-header {
  text-align: center;
  margin-bottom: 48px;
}

.section-header h2 {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.section-header p {
  color: #94a3b8;
  font-size: 1.1rem;
}

.pricing-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
}

.pricing-card {
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 16px;
  overflow: hidden;
  position: relative;
  transition: all 0.3s;
}

.pricing-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
}

.pricing-card.recommended {
  border-color: #a78bfa;
  box-shadow: 0 0 30px rgba(167, 139, 250, 0.2);
}

.recommended-badge {
  position: absolute;
  top: 16px;
  right: -32px;
  background: linear-gradient(135deg, #f472b6, #a78bfa);
  color: white;
  padding: 6px 40px;
  font-size: 0.85rem;
  font-weight: 600;
  transform: rotate(45deg);
  box-shadow: 0 4px 10px rgba(244, 114, 182, 0.4);
}

.card-header {
  padding: 32px 24px;
  text-align: center;
}

.card-header i {
  font-size: 3rem;
  margin-bottom: 16px;
}

.card-header h3 {
  font-size: 1.5rem;
}

.monthly {
  background: linear-gradient(135deg, #34d39920, #10b98120);
  color: #34d399;
}

.quarterly {
  background: linear-gradient(135deg, #fbbf2420, #f59e0b20);
  color: #fbbf24;
}

.yearly {
  background: linear-gradient(135deg, #a78bfa20, #7c3aed20);
  color: #a78bfa;
}

.lifetime {
  background: linear-gradient(135deg, #f472b620, #ec489920);
  color: #f472b6;
}

.card-body {
  padding: 32px 24px;
}

.price {
  text-align: center;
  margin-bottom: 16px;
}

.currency {
  font-size: 1.5rem;
  vertical-align: top;
}

.amount {
  font-size: 3.5rem;
  font-weight: 700;
}

.original-price-inline {
  display: block;
  color: #64748b;
  text-decoration: line-through;
  font-size: 1rem;
  margin-bottom: 4px;
}

.period {
  color: #94a3b8;
  font-size: 1rem;
}

.savings {
  text-align: center;
  color: #10b981;
  font-size: 0.9rem;
  margin-bottom: 24px;
  padding: 8px;
  background: #10b98110;
  border-radius: 8px;
}

.features {
  list-style: none;
  padding: 0;
  margin-bottom: 32px;
}

.features li {
  padding: 12px 0;
  border-bottom: 1px solid #2a3748;
  display: flex;
  align-items: center;
  gap: 12px;
  color: #cbd5e1;
}

.features li:last-child {
  border-bottom: none;
}

.features li i {
  color: #10b981;
}

.features li.disabled {
  color: #64748b;
}

.features li.disabled i {
  color: #ef4444;
}

.subscribe-btn {
  width: 100%;
  padding: 14px;
  border: none;
  border-radius: 12px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  color: white;
}

.subscribe-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.3);
}

.subscribe-btn.monthly {
  background: linear-gradient(135deg, #34d399, #10b981);
}

.subscribe-btn.quarterly {
  background: linear-gradient(135deg, #fbbf24, #f59e0b);
}

.subscribe-btn.yearly {
  background: linear-gradient(135deg, #a78bfa, #7c3aed);
}

.subscribe-btn.lifetime {
  background: linear-gradient(135deg, #f472b6, #ec4899);
}

/* 特权部分 */
.benefits-section {
  margin-bottom: 64px;
}

.benefits-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 24px;
}

.benefit-card {
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 16px;
  padding: 32px;
  text-align: center;
  transition: all 0.2s;
}

.benefit-card:hover {
  border-color: #a78bfa;
  transform: translateY(-4px);
}

.benefit-icon {
  font-size: 3rem;
  color: #a78bfa;
  margin-bottom: 16px;
}

.benefit-card h3 {
  margin-bottom: 12px;
  font-size: 1.3rem;
}

.benefit-card p {
  color: #94a3b8;
  line-height: 1.6;
}

/* FAQ 部分 */
.faq-section {
  margin-bottom: 64px;
}

.faq-list {
  max-width: 800px;
  margin: 0 auto;
}

.faq-item {
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 12px;
  margin-bottom: 16px;
  overflow: hidden;
}

.faq-question {
  padding: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  transition: background 0.2s;
}

.faq-question:hover {
  background: #1e2a3a;
}

.faq-question h3 {
  font-size: 1.1rem;
  font-weight: 600;
}

.faq-question i {
  transition: transform 0.3s;
  color: #94a3b8;
}

.faq-question i.active {
  transform: rotate(180deg);
  color: #a78bfa;
}

.faq-answer {
  padding: 0 24px 24px;
  color: #cbd5e1;
  line-height: 1.8;
}

/* 弹窗 */
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
  width: 90%;
  max-width: 450px;
}

.modal-header {
  padding: 24px 32px;
  border-bottom: 1px solid #2a3748;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h2 {
  font-size: 1.5rem;
  display: flex;
  align-items: center;
  gap: 12px;
}

.close-btn {
  background: none;
  border: none;
  color: #94a3b8;
  font-size: 1.5rem;
  cursor: pointer;
}

.close-btn:hover {
  color: #edf2f7;
}

.modal-body {
  padding: 32px;
  text-align: center;
  color: #cbd5e1;
  font-size: 1.1rem;
}

.modal-footer {
  padding: 24px 32px;
  border-top: 1px solid #2a3748;
  display: flex;
  gap: 16px;
  justify-content: flex-end;
}

.btn-secondary,
.btn-primary {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-secondary {
  background: #2a3748;
  color: #cbd5e1;
}

.btn-secondary:hover {
  background: #3a4758;
}

.btn-primary {
  background: linear-gradient(135deg, #7c3aed, #a78bfa);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 14px rgba(124, 58, 237, 0.4);
}

.payment-modal {
  max-width: 480px;
}

.payment-body {
  text-align: center;
}

.payment-plan-name {
  font-size: 1.3rem;
  font-weight: 600;
  margin-bottom: 16px;
}

.payment-amount {
  margin-bottom: 12px;
}

.payment-amount .amount {
  font-size: 2.8rem;
  font-weight: 700;
}

.payment-savings {
  color: #10b981;
  background: #10b98115;
  padding: 8px 12px;
  border-radius: 8px;
  margin-bottom: 16px;
}

.payment-hint {
  color: #94a3b8;
  font-size: 0.9rem;
  line-height: 1.6;
}

.payment-error {
  color: #ef4444;
  background: #ef444420;
  padding: 10px 12px;
  border-radius: 8px;
  margin-bottom: 12px;
  font-size: 0.9rem;
}

.payment-processing,
.payment-success {
  padding: 24px 0;
  color: #cbd5e1;
}

.payment-processing i,
.payment-success i {
  font-size: 3rem;
  margin-bottom: 16px;
  display: block;
}

.payment-processing i { color: #a78bfa; }
.payment-success i { color: #10b981; }

.payment-processing small,
.payment-success small {
  color: #94a3b8;
}
</style>
