<template>
  <div class="avatar-wrapper" ref="wrapperRef">
    <button class="avatar-btn" type="button" @click="toggleDropdown">
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
        <i class="fas fa-user-check"></i>
        欢迎，{{ userStore.currentUser }}
        <br/>
        <small style="color: #a78bfa;">
          {{ permissionName }}
          <span v-if="userStore.vipExpiryTime && userStore.vipExpiryTime !== '永久'">
            (至 {{ userStore.vipExpiryTime }})
          </span>
        </small>
      </div>

      <button
          v-if="userStore.isLoggedIn"
          class="dropdown-item"
          :class="{ active: isUserCenterActive }"
          @click="goToUserCenter"
      >
        <i class="fas fa-user-cog"></i> 用户中心
      </button>

      <slot name="before-actions" />

      <button
          v-if="userStore.isAdmin && showAdminDashboard"
          class="dropdown-item"
          @click="navigate('/admin-dashboard')"
      >
        <i class="fas fa-chart-pie"></i> 管理仪表盘
      </button>

      <button
          v-if="userStore.isAdmin && showAdminGames"
          class="dropdown-item"
          @click="navigate('/admin-games')"
      >
        <i class="fas fa-database"></i> 游戏管理
      </button>

      <button
          v-if="userStore.isAdmin && showAdminFeedback"
          class="dropdown-item"
          @click="navigate('/admin-feedback')"
      >
        <i class="fas fa-inbox"></i> 反馈管理
      </button>

      <button
          v-if="userStore.isLoggedIn && showSubscription"
          class="dropdown-item"
          @click="navigate('/subscription')"
      >
        <i class="fas fa-crown"></i> 我的订阅
      </button>

      <button
          v-if="userStore.isLoggedIn"
          class="dropdown-item"
          @click="handleLogout"
      >
        <i class="fas fa-sign-out-alt"></i> 退出账号
      </button>

      <button
          v-else
          class="dropdown-item"
          @click="handleLogin"
      >
        <i class="fas fa-sign-in-alt"></i> 登录
      </button>

      <div v-if="showDivider" class="divider"></div>

      <slot name="after-actions" />

      <button
          v-if="showBrowseHistory"
          class="dropdown-item"
          @click="handleBrowseHistory"
      >
        <i class="fas fa-history"></i> 查看浏览记录
      </button>

      <button
          v-if="showHomeLink"
          class="dropdown-item"
          @click="navigate('/home')"
      >
        <i class="fas fa-home"></i> 返回首页
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const props = defineProps({
  showSubscription: { type: Boolean, default: true },
  showBrowseHistory: { type: Boolean, default: false },
  showHomeLink: { type: Boolean, default: false },
  showDivider: { type: Boolean, default: true },
  showAdminDashboard: { type: Boolean, default: false },
  showAdminGames: { type: Boolean, default: false },
  showAdminFeedback: { type: Boolean, default: false }
})

const emit = defineEmits(['login', 'browse-history'])

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const showDropdown = ref(false)
const wrapperRef = ref(null)

const isUserCenterActive = computed(() => route.path === '/user-center')

const permissionIcon = computed(() => {
  switch (userStore.permissionLevel) {
    case 'LIFETIME': return '<i class="fas fa-crown" style="color: #f472b6; font-size: 0.8rem;"></i>'
    case 'YEARLY': return '<i class="fas fa-gem" style="color: #a78bfa; font-size: 0.8rem;"></i>'
    case 'QUARTERLY': return '<i class="fas fa-star" style="color: #fbbf24; font-size: 0.8rem;"></i>'
    case 'MONTHLY': return '<i class="fas fa-check-circle" style="color: #34d399; font-size: 0.8rem;"></i>'
    default: return '<i class="fas fa-user" style="color: #94a3b8; font-size: 0.8rem;"></i>'
  }
})

const permissionName = computed(() => {
  if (userStore.isAdmin) return '管理员（永久VIP）'
  switch (userStore.permissionLevel) {
    case 'LIFETIME': return '终身会员'
    case 'YEARLY': return '年度会员'
    case 'QUARTERLY': return '季度会员'
    case 'MONTHLY': return '月度会员'
    default: return '免费用户'
  }
})

function toggleDropdown() {
  showDropdown.value = !showDropdown.value
}

function closeDropdown() {
  showDropdown.value = false
}

function navigate(path) {
  closeDropdown()
  router.push(path)
}

function goToUserCenter() {
  if (!userStore.isLoggedIn) {
    handleLogin()
    return
  }
  navigate('/user-center')
}

function handleLogin() {
  closeDropdown()
  emit('login')
}

function handleBrowseHistory() {
  closeDropdown()
  emit('browse-history')
}

async function handleLogout() {
  closeDropdown()
  await userStore.logout()
}

function handleDocumentClick(event) {
  if (wrapperRef.value && !wrapperRef.value.contains(event.target)) {
    closeDropdown()
  }
}

onMounted(() => {
  document.addEventListener('click', handleDocumentClick)
})

onBeforeUnmount(() => {
  document.removeEventListener('click', handleDocumentClick)
})
</script>

<style scoped>
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
  min-width: 220px;
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
  display: flex;
  align-items: center;
  gap: 8px;
}

.dropdown-item:hover,
.dropdown-item.active {
  background: #1e2a3a;
  color: #a78bfa;
}

.divider {
  height: 1px;
  background: #2a3748;
  margin: 8px 0;
}
</style>
