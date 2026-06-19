<template>
  <div class="home-container">
    <!-- 导航栏 -->
    <header class="header">
      <div class="logo">
        <i class="fas fa-gamepad" style="margin-right: 6px; color: #c084fc;"></i>
        GAMEVILA
      </div>

      <nav class="nav-links">
        <router-link to="/home" class="active">首页</router-link>
        <router-link to="/about">关于</router-link>
        <router-link to="/service">服务</router-link>
        <router-link to="/contact">联系</router-link>

        <!-- 管理员专属按钮 -->
        <router-link
            v-if="userStore.isAdmin"
            to="/admin-dashboard"
            class="admin-btn dashboard-btn"
        >
          <i class="fas fa-chart-pie"></i> 仪表盘
        </router-link>

        <router-link
            v-if="userStore.isAdmin"
            to="/admin-games"
            class="admin-btn games-admin-btn"
        >
          <i class="fas fa-database"></i> 游戏管理
          <i class="fas fa-arrow-right"></i>
        </router-link>

        <router-link
            v-if="userStore.isAdmin"
            to="/admin-feedback"
            class="admin-btn"
        >
          <i class="fas fa-inbox"></i> 查看反馈
          <i class="fas fa-arrow-right"></i>
        </router-link>

        <router-link to="/service" class="buy-now">
          订阅VIP <i class="fas fa-arrow-right"></i>
        </router-link>
      </nav>

      <!-- 用户头像区域 -->
      <UserAvatarMenu
          :show-admin-dashboard="userStore.isAdmin"
          :show-admin-games="userStore.isAdmin"
          :show-admin-feedback="userStore.isAdmin"
          show-browse-history
          @login="openLoginModal"
          @browse-history="openHistoryPanel"
      />
    </header>

    <VipExpiryBanner />

    <!-- Hero 区域 -->
    <section class="hero">
      <div class="hero-content">
        <div class="hero-badge">🎮 设计 · 研究 · 玩乐</div>
        <h1 class="hero-title">你的游戏<span>新主场</span></h1>
        <p class="hero-desc">
          Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor
          incididunt ut labore et dolore magna aliqua. 精选独立游戏，即刻开玩。
        </p>

        <!-- 搜索框 -->
        <div class="search-container">
          <input
              type="text"
              v-model="searchKeyword"
              @input="handleSearchInput"
              @focus="handleSearchFocus"
              @blur="handleSearchBlur"
              @keydown.enter="handleSearchEnter"
              class="search-input"
              placeholder="搜索游戏名称..."
              autocomplete="off"
          />
          <i class="fas fa-search search-icon"></i>

          <div v-if="showSearchPanel" class="search-panel">
            <template v-if="searchKeyword.trim() && suggestions.length > 0">
              <div class="panel-section-title">搜索建议</div>
              <div
                  v-for="(suggestion, index) in suggestions"
                  :key="'s-' + index"
                  class="panel-item"
                  @mousedown.prevent="selectKeyword(suggestion)"
              >
                <i class="fas fa-search"></i>
                <span>{{ suggestion }}</span>
              </div>
            </template>

            <template v-if="!searchKeyword.trim() && searchHistory.length > 0">
              <div class="panel-section-header">
                <span class="panel-section-title">搜索历史</span>
                <button class="panel-clear-btn" @mousedown.prevent="clearHistory">清空</button>
              </div>
              <div
                  v-for="item in searchHistory"
                  :key="'h-' + item.keyword"
                  class="panel-item history-item"
                  @mousedown.prevent="selectKeyword(item.keyword)"
              >
                <i class="fas fa-history"></i>
                <span>{{ item.keyword }}</span>
                <button
                    class="remove-history-btn"
                    @mousedown.prevent.stop="removeSearchHistoryItem(item.keyword)"
                >
                  <i class="fas fa-times"></i>
                </button>
              </div>
            </template>

            <template v-if="popularSearches.length > 0 && (!searchKeyword.trim() || suggestions.length === 0)">
              <div class="panel-section-title">
                <i class="fas fa-fire" style="color: #f97316;"></i> 热门搜索
              </div>
              <div class="popular-tags">
                <button
                    v-for="item in popularSearches"
                    :key="'p-' + item.keyword"
                    class="popular-tag"
                    @mousedown.prevent="selectKeyword(item.keyword)"
                >
                  {{ item.keyword }}
                  <span v-if="item.searchCount" class="tag-count">{{ item.searchCount }}</span>
                </button>
              </div>
            </template>

            <div
                v-if="!searchKeyword.trim() && searchHistory.length === 0 && popularSearches.length === 0"
                class="panel-empty"
            >
              输入关键词开始搜索
            </div>
          </div>
        </div>

        <button class="btn-discover" @click="scrollToGames">
          发现更多 <i class="fas fa-arrow-right"></i>
        </button>
      </div>

      <div class="hero-visual">
        <i class="fas fa-crown"></i>
      </div>
    </section>

    <!-- 热门推荐标题 -->
    <div class="section-title">
      <span>
        <i class="fas fa-fire" style="color: #f97316; margin-right: 10px;"></i>
        热门推荐
      </span>
      <span style="font-size: 1rem; color: #9ca3af;">点击卡片记录浏览</span>
    </div>

    <!-- 分类标签 -->
    <div class="category-tabs">
      <button
          v-for="category in categories"
          :key="category.value"
          class="category-tab"
          :class="{ active: currentCategory === category.value }"
          @click="filterByCategory(category.value)"
      >
        {{ category.label }}
      </button>
    </div>

    <!-- 游戏列表 -->
    <div class="game-grid">
      <div
          v-if="games.length === 0"
          class="no-games"
      >
        未找到相关游戏
      </div>

      <div
          v-for="game in games"
          :key="game.id"
          class="game-card"
          @click="recordGameVisit(game)"
      >
        <div
            class="game-img"
            :style="{ background: game.imageUrl ? '#1e2a3a' : '#3b2e5c' }"
        >
          <button
              class="favorite-btn"
              :class="{ active: isFavorited(game.id) }"
              :title="isFavorited(game.id) ? '取消收藏' : '收藏'"
              @click.stop="toggleFavorite(game)"
          >
            <i :class="isFavorited(game.id) ? 'fas fa-heart' : 'far fa-heart'"></i>
          </button>
          <img
              v-if="game.imageUrl"
              :src="game.imageUrl"
              :alt="game.name"
              class="img-placeholder"
          />
          <i
              v-else
              class="fas fa-gamepad"
              style="font-size: 3.5rem; color: #e2e8f0; opacity: 0.9;"
          ></i>
        </div>

        <div class="game-info">
          <div class="game-title">{{ game.name }}</div>
          <div class="game-tag">
            <i class="fas fa-tag"></i>
            {{ game.description || '暂无简介' }}
          </div>
        </div>
      </div>
    </div>

    <!-- 登录弹窗 -->
    <div v-if="showLoginModal" class="modal-overlay" @click="closeLoginModal">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h2>登录账号</h2>
          <button class="close-btn" @click="closeLoginModal">
            <i class="fas fa-times"></i>
          </button>
        </div>

        <form @submit.prevent="handleLogin">
          <div class="form-group">
            <label>用户名</label>
            <input
                type="text"
                v-model="loginForm.username"
                required
                placeholder="请输入用户名"
            />
          </div>

          <div class="form-group">
            <label>密码</label>
            <input
                type="password"
                v-model="loginForm.password"
                required
                placeholder="请输入密码"
            />
          </div>

          <div v-if="loginError" class="error-message">
            {{ loginError }}
          </div>

          <button type="submit" class="submit-btn" :disabled="loginLoading">
            <span v-if="loginLoading">登录中...</span>
            <span v-else>登录</span>
          </button>
        </form>

        <div class="modal-footer">
          还没有账号？
          <a href="#" @click.prevent="switchToRegister">立即注册</a>
        </div>
      </div>
    </div>

    <!-- 注册弹窗 -->
    <div v-if="showRegisterModal" class="modal-overlay" @click="closeRegisterModal">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h2>注册账号</h2>
          <button class="close-btn" @click="closeRegisterModal">
            <i class="fas fa-times"></i>
          </button>
        </div>

        <form @submit.prevent="handleRegister">
          <div class="form-group">
            <label>用户名</label>
            <input
                type="text"
                v-model="registerForm.username"
                required
                placeholder="请输入用户名"
            />
          </div>

          <div class="form-group">
            <label>邮箱（可选）</label>
            <input
                type="email"
                v-model="registerForm.email"
                placeholder="请输入邮箱"
            />
          </div>

          <div class="form-group">
            <label>密码</label>
            <input
                type="password"
                v-model="registerForm.password"
                required
                placeholder="至少6位字符"
            />
          </div>

          <div class="form-group">
            <label>确认密码</label>
            <input
                type="password"
                v-model="registerForm.confirmPassword"
                required
                placeholder="请再次输入密码"
            />
          </div>

          <div v-if="registerError" class="error-message">
            {{ registerError }}
          </div>

          <div v-if="registerSuccess" class="success-message">
            {{ registerSuccess }}
          </div>

          <button type="submit" class="submit-btn" :disabled="registerLoading">
            <span v-if="registerLoading">注册中...</span>
            <span v-else>注册</span>
          </button>
        </form>

        <div class="modal-footer">
          已有账号？
          <a href="#" @click.prevent="switchToLogin">立即登录</a>
        </div>
      </div>
    </div>

    <!-- 浏览记录面板 -->
    <div v-if="showHistoryPanel" class="history-panel-overlay" @click="closeHistoryPanel">
      <div class="history-panel" @click.stop>
        <div class="panel-header">
          <h3><i class="fas fa-history"></i> 浏览记录</h3>
          <button class="close-btn" @click="closeHistoryPanel">
            <i class="fas fa-times"></i>
          </button>
        </div>

        <div class="panel-body">
          <div v-if="userStore.isLoggedIn && libraryLimits" class="history-limit-hint">
            已保存 {{ libraryLimits.browseHistoryCount }} / {{ libraryLimits.maxBrowseHistory }} 条
            <span v-if="libraryLimits.tier === 'FREE'">（开通 VIP 可提升至 100 条）</span>
          </div>

          <div v-if="browseHistory.length === 0" class="empty-history">
            暂无浏览记录
          </div>

          <div
              v-for="(item, index) in browseHistory"
              :key="item.historyId || item.id || index"
              class="history-item clickable"
              @click="openFromHistory(item)"
          >
            <div class="history-name">
              {{ item.name }}
              <span v-if="item.vipOnly && item.canAccess === false" class="vip-lock-tag">
                <i class="fas fa-lock"></i> VIP
              </span>
            </div>
            <div class="history-time">{{ formatTime(item.time || item.viewedAt) }}</div>
            <button class="remove-history-btn" title="删除" @click.stop="removeHistoryItem(item)">
              <i class="fas fa-times"></i>
            </button>
          </div>
        </div>

        <div v-if="browseHistory.length > 0" class="panel-footer">
          <button class="clear-history-btn" @click="clearAllBrowseHistory">清空记录</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import { gameApi } from '../api/games'
import { userApi } from '../api/user'
import VipExpiryBanner from '../components/VipExpiryBanner.vue'
import UserAvatarMenu from '../components/UserAvatarMenu.vue'
import {
  loadSearchHistory,
  saveSearchKeyword,
  removeSearchKeyword,
  clearSearchHistory
} from '../utils/searchHistory'
import {
  loadLocalBrowseHistory,
  recordLocalBrowse,
  removeLocalBrowse,
  clearLocalBrowseHistory,
  getLocalBrowseHistoryForSync,
  clearLocalBrowseHistoryStorage
} from '../utils/browseHistory'
import { showVipReminderOnLogin } from '../utils/vipReminder'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 数据状态
const games = ref([])
const allGames = ref([])
const currentCategory = ref('all')
const searchKeyword = ref('')
const showSearchPanel = ref(false)
const suggestions = ref([])
const searchHistory = ref([])
const popularSearches = ref([])
const showDropdown = ref(false)
const showLoginModal = ref(false)
const showRegisterModal = ref(false)
const showHistoryPanel = ref(false)
const loginLoading = ref(false)
const registerLoading = ref(false)
const loginError = ref('')
const registerError = ref('')
const registerSuccess = ref('')
const browseHistory = ref([])
const favoriteIds = ref([])
const libraryLimits = ref(null)

// 表单数据
const loginForm = ref({
  username: '',
  password: ''
})

const registerForm = ref({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

// 分类列表
const categories = [
  { value: 'all', label: '全部' },
  { value: '冒险', label: '冒险' },
  { value: 'RPG', label: 'RPG' },
  { value: '竞速', label: '竞速' },
  { value: '解谜', label: '解谜' },
  { value: '射击', label: '射击' },
  { value: '模拟', label: '模拟' },
  { value: '策略', label: '策略' }
]

// 计算属性
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

// 生命周期
onMounted(async () => {
  await userStore.checkLoginStatus()
  await loadGames()
  await syncBrowseHistoryIfNeeded()
  await loadBrowseHistory()
  refreshSearchHistory()
  await loadPopularSearches()
  await loadFavoriteIds()
  await loadLibraryLimits()
  if (route.query.history === '1') {
    openHistoryPanel()
  }
})

async function loadPopularSearches() {
  try {
    const response = await gameApi.getPopularSearches(8)
    popularSearches.value = response.data
  } catch (error) {
    console.error('加载热门搜索失败:', error)
  }
}

function refreshSearchHistory() {
  searchHistory.value = loadSearchHistory()
}

// 方法
async function loadGames() {
  try {
    const response = await gameApi.getAllGames()
    allGames.value = response.data
    games.value = response.data
  } catch (error) {
    console.error('加载游戏失败:', error)
  }
}

async function filterByCategory(category) {
  currentCategory.value = category

  if (category === 'all') {
    games.value = allGames.value
  } else {
    try {
      const response = await gameApi.getGamesByCategory(category)
      games.value = response.data
    } catch (error) {
      console.error('获取分类游戏失败:', error)
      games.value = []
    }
  }
}

let searchTimeout = null
let autocompleteTimeout = null

function handleSearchInput() {
  if (searchTimeout) clearTimeout(searchTimeout)
  if (autocompleteTimeout) clearTimeout(autocompleteTimeout)

  const keyword = searchKeyword.value.trim()

  if (!keyword) {
    filterByCategory(currentCategory.value)
    suggestions.value = []
    showSearchPanel.value = searchHistory.value.length > 0 || popularSearches.value.length > 0
    return
  }

  showSearchPanel.value = true

  autocompleteTimeout = setTimeout(async () => {
    try {
      const response = await gameApi.getAutocompleteSuggestions(keyword)
      suggestions.value = response.data
    } catch (error) {
      console.error('获取搜索建议失败:', error)
      suggestions.value = []
    }
  }, 150)

  searchTimeout = setTimeout(async () => {
    await performSearch(keyword, false)
  }, 300)
}

function handleSearchFocus() {
  showSearchPanel.value = true
  if (!searchKeyword.value.trim()) {
    refreshSearchHistory()
  }
}

function handleSearchBlur() {
  setTimeout(() => {
    showSearchPanel.value = false
  }, 150)
}

async function handleSearchEnter() {
  const keyword = searchKeyword.value.trim()
  if (!keyword) return
  await performSearch(keyword, true)
  showSearchPanel.value = false
}

async function performSearch(keyword, saveHistory = true) {
  try {
    const response = await gameApi.searchGames(keyword)
    games.value = response.data
    if (saveHistory) {
      saveSearchKeyword(keyword)
      refreshSearchHistory()
    }
  } catch (error) {
    console.error('搜索失败:', error)
  }
}

async function selectKeyword(keyword) {
  searchKeyword.value = keyword
  showSearchPanel.value = false
  await performSearch(keyword, true)
}

function removeSearchHistoryItem(keyword) {
  removeSearchKeyword(keyword)
  refreshSearchHistory()
}

function clearHistory() {
  clearSearchHistory()
  refreshSearchHistory()
}

function toggleDropdown() {
  showDropdown.value = !showDropdown.value
}

function closeDropdown() {
  showDropdown.value = false
}

function openLoginModal() {
  closeDropdown()
  showLoginModal.value = true
  loginForm.value = { username: '', password: '' }
  loginError.value = ''
}

function closeLoginModal() {
  showLoginModal.value = false
  loginForm.value = { username: '', password: '' }
  loginError.value = ''
}

async function handleLogin() {
  loginLoading.value = true
  loginError.value = ''

  const result = await userStore.login(
      loginForm.value.username,
      loginForm.value.password
  )

  loginLoading.value = false

  if (result.success) {
    closeLoginModal()
    showVipReminderOnLogin(userStore)
    await syncBrowseHistoryIfNeeded()
    await loadBrowseHistory()
    await loadFavoriteIds()
    await loadLibraryLimits()
  } else {
    loginError.value = result.message || '登录失败'
  }
}

function switchToRegister() {
  closeLoginModal()
  setTimeout(() => {
    showRegisterModal.value = true
  }, 100)
}

function openRegisterModal() {
  closeDropdown()
  showRegisterModal.value = true
  registerForm.value = { username: '', email: '', password: '', confirmPassword: '' }
  registerError.value = ''
  registerSuccess.value = ''
}

function closeRegisterModal() {
  showRegisterModal.value = false
  registerForm.value = { username: '', email: '', password: '', confirmPassword: '' }
  registerError.value = ''
  registerSuccess.value = ''
}

async function handleRegister() {
  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    registerError.value = '两次输入的密码不一致'
    return
  }

  if (registerForm.value.password.length < 6) {
    registerError.value = '密码长度至少为6位'
    return
  }

  registerLoading.value = true
  registerError.value = ''
  registerSuccess.value = ''

  try {
    const response = await fetch('/api/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'same-origin',
      body: JSON.stringify({
        username: registerForm.value.username.trim(),
        email: registerForm.value.email ? registerForm.value.email.trim() : null,
        password: registerForm.value.password
      })
    })

    const data = await response.json()

    if (response.ok && data.success) {
      await userStore.checkLoginStatus()
      closeRegisterModal()
      //alert(`注册成功！欢迎 ${data.username}`)
    } else {
      registerError.value = data.message || '注册失败'
    }
  } catch (error) {
    registerError.value = '网络错误，请稍后重试'
  } finally {
    registerLoading.value = false
  }
}

function switchToLogin() {
  closeRegisterModal()
  setTimeout(() => {
    showLoginModal.value = true
  }, 100)
}

async function handleLogout() {
  await userStore.logout()
  closeDropdown()
  alert('已退出登录')
}

function goToAdminDashboard() {
  closeDropdown()
  router.push('/admin-dashboard')
}

function goToAdminGames() {
  closeDropdown()
  router.push('/admin-games')
}

function goToAdminFeedback() {
  closeDropdown()
  router.push('/admin-feedback')
}

function goToUserCenter() {
  closeDropdown()
  router.push('/user-center')
}

function goToService() {
  closeDropdown()
  router.push('/service')
}

function openHistoryPanel() {
  closeDropdown()
  loadBrowseHistory()
  showHistoryPanel.value = true
}

function closeHistoryPanel() {
  showHistoryPanel.value = false
}

async function syncBrowseHistoryIfNeeded() {
  if (!userStore.isLoggedIn) return

  const items = getLocalBrowseHistoryForSync()
  if (items.length === 0) return

  try {
    await userApi.syncBrowseHistory(items)
    clearLocalBrowseHistoryStorage()
  } catch (error) {
    console.error('同步浏览记录失败:', error)
  }
}

async function loadLibraryLimits() {
  if (!userStore.isLoggedIn) {
    libraryLimits.value = null
    return
  }

  try {
    const response = await userApi.getLibraryLimits()
    libraryLimits.value = response.data
  } catch (error) {
    console.error('加载库容量失败:', error)
  }
}

function mapServerHistory(items) {
  return items.map(item => ({
    ...item,
    id: item.gameId,
    time: item.viewedAt
  }))
}

async function recordGameVisit(game) {
  await recordBrowseForGame(game)

  if (game.vipOnly && !userStore.hasValidPermission) {
    if (!userStore.isLoggedIn) {
      openLoginModal()
      return
    }
    if (confirm('该游戏需要 VIP 权限，是否前往开通？')) {
      router.push('/service')
    }
    return
  }

  if (game.link && game.link !== '#') {
    window.open(game.link, '_blank')
  }
}

async function recordBrowseForGame(game) {
  if (userStore.isLoggedIn) {
    try {
      const response = await userApi.recordBrowse(game.id)
      if (response.data.success && response.data.item) {
        const item = response.data.item
        const mapped = mapServerHistory([item])[0]
        browseHistory.value = [
          mapped,
          ...browseHistory.value.filter(entry => entry.id !== game.id)
        ]
        if (response.data.maxBrowseHistory) {
          libraryLimits.value = {
            ...libraryLimits.value,
            ...response.data
          }
        }
      } else {
        await loadBrowseHistory()
      }
    } catch (error) {
      console.error('记录浏览失败:', error)
    }
    return
  }

  browseHistory.value = recordLocalBrowse(game)
}

async function loadBrowseHistory() {
  if (userStore.isLoggedIn) {
    try {
      const response = await userApi.getBrowseHistory()
      browseHistory.value = mapServerHistory(response.data)
    } catch (error) {
      console.error('加载浏览记录失败:', error)
      browseHistory.value = []
    }
    return
  }

  browseHistory.value = loadLocalBrowseHistory()
}

function openFromHistory(item) {
  if (item.vipOnly && item.canAccess === false) {
    if (!userStore.isLoggedIn) {
      openLoginModal()
      return
    }
    if (confirm('该游戏需要 VIP 权限，是否前往开通？')) {
      router.push('/service')
    }
    return
  }

  if (item.link && item.link !== '#') {
    window.open(item.link, '_blank')
  }
}

async function removeHistoryItem(item) {
  const gameId = item.gameId || item.id
  if (userStore.isLoggedIn) {
    try {
      await userApi.removeBrowseHistory(gameId)
      browseHistory.value = browseHistory.value.filter(entry => entry.id !== gameId)
      await loadLibraryLimits()
    } catch (error) {
      alert('删除失败')
    }
    return
  }

  browseHistory.value = removeLocalBrowse(gameId)
}

async function clearAllBrowseHistory() {
  if (!confirm('确定清空全部浏览记录？')) return

  if (userStore.isLoggedIn) {
    try {
      await userApi.clearBrowseHistory()
      browseHistory.value = []
      await loadLibraryLimits()
    } catch (error) {
      alert('清空失败')
    }
    return
  }

  browseHistory.value = clearLocalBrowseHistory()
}

async function loadFavoriteIds() {
  if (!userStore.isLoggedIn) {
    favoriteIds.value = []
    return
  }

  try {
    const response = await userApi.getFavoriteIds()
    favoriteIds.value = response.data
  } catch (error) {
    console.error('加载收藏失败:', error)
  }
}

function isFavorited(gameId) {
  return favoriteIds.value.includes(gameId)
}

async function toggleFavorite(game) {
  if (!userStore.isLoggedIn) {
    closeDropdown()
    openLoginModal()
    return
  }

  try {
    if (isFavorited(game.id)) {
      const response = await userApi.removeFavorite(game.id)
      if (response.data.success) {
        favoriteIds.value = favoriteIds.value.filter(id => id !== game.id)
      }
    } else {
      const response = await userApi.addFavorite(game.id)
      if (response.data.success) {
        favoriteIds.value = [...favoriteIds.value, game.id]
        if (response.data.maxFavorites) {
          libraryLimits.value = { ...libraryLimits.value, ...response.data }
        }
      } else {
        alert(response.data.message || '收藏失败')
      }
    }
  } catch (error) {
    alert(error.response?.data?.message || '操作失败')
  }
}

function formatTime(isoString) {
  const date = new Date(isoString)
  return date.toLocaleString('zh-CN')
}

function scrollToGames() {
  document.querySelector('.section-title')?.scrollIntoView({ behavior: 'smooth' })
}

// 点击外部关闭下拉菜单
watch(showDropdown, (value) => {
  if (value) {
    setTimeout(() => {
      document.addEventListener('click', closeDropdown)
    }, 0)
  } else {
    document.removeEventListener('click', closeDropdown)
  }
})
</script>

<style scoped>
.home-container {
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
  transition: all 0.2s;
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
  transition: all 0.2s;
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
  transition: all 0.2s;
}

.avatar-btn:hover {
  border-color: #a78bfa;
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
  color: #edf2f7;
}

.dropdown-item {
  width: 100%;
  padding: 12px 16px;
  background: none;
  border: none;
  color: #cbd5e1;
  text-align: left;
  cursor: pointer;
  transition: background 0.2s;
}

.dropdown-item:hover {
  background: #1e2a3a;
}

.divider {
  height: 1px;
  background: #2a3748;
  margin: 8px 0;
}

.hero {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 48px;
  margin-bottom: 64px;
}

.hero-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.hero-badge {
  display: inline-block;
  background: #161e2a;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 0.9rem;
  width: fit-content;
}

.hero-title {
  font-size: 3.5rem;
  font-weight: 700;
  line-height: 1.2;
}

.hero-title span {
  background: linear-gradient(135deg, #a78bfa, #f472b6);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.hero-desc {
  color: #94a3b8;
  font-size: 1.1rem;
  line-height: 1.8;
}

.search-container {
  position: relative;
}

.search-input {
  width: 100%;
  padding: 14px 48px 14px 20px;
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 12px;
  color: #edf2f7;
  font-size: 1rem;
}

.search-icon {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  color: #64748b;
}

.search-container {
  position: relative;
  max-width: 500px;
}

.search-panel {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  margin-top: 8px;
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 12px;
  overflow: hidden;
  z-index: 100;
  max-height: 360px;
  overflow-y: auto;
  padding: 8px 0;
}

.panel-section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 16px 4px;
}

.panel-section-title {
  padding: 8px 16px 4px;
  font-size: 0.8rem;
  color: #64748b;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 6px;
}

.panel-clear-btn {
  background: none;
  border: none;
  color: #94a3b8;
  font-size: 0.8rem;
  cursor: pointer;
}

.panel-clear-btn:hover { color: #ef4444; }

.panel-item {
  padding: 10px 16px;
  cursor: pointer;
  transition: background 0.2s;
  display: flex;
  align-items: center;
  gap: 10px;
  color: #cbd5e1;
}

.panel-item i { color: #64748b; width: 16px; text-align: center; }

.panel-item:hover { background: #1e2a3a; }

.history-item {
  justify-content: space-between;
}

.remove-history-btn {
  background: none;
  border: none;
  color: #64748b;
  cursor: pointer;
  padding: 4px;
}

.remove-history-btn:hover { color: #ef4444; }

.popular-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 8px 16px 12px;
}

.popular-tag {
  background: #1e2a3a;
  border: 1px solid #2a3748;
  border-radius: 20px;
  padding: 6px 14px;
  color: #cbd5e1;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s;
}

.popular-tag:hover {
  border-color: #7c3aed;
  color: #a78bfa;
}

.tag-count {
  margin-left: 6px;
  font-size: 0.75rem;
  color: #f97316;
  font-weight: 600;
}

.panel-empty {
  padding: 20px 16px;
  text-align: center;
  color: #64748b;
  font-size: 0.9rem;
}

.autocomplete-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  margin-top: 8px;
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 12px;
  overflow: hidden;
  z-index: 100;
}

.autocomplete-item {
  padding: 12px 20px;
  cursor: pointer;
  transition: background 0.2s;
}

.autocomplete-item:hover {
  background: #1e2a3a;
}

.btn-discover {
  background: linear-gradient(145deg, #7c3aed, #a78bfa);
  padding: 14px 32px;
  border: none;
  border-radius: 12px;
  color: white;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  width: fit-content;
}

.btn-discover:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(124, 58, 237, 0.4);
}

.hero-visual {
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-visual i {
  font-size: 12rem;
  background: linear-gradient(135deg, #a78bfa20, #f472b620);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  font-size: 1.5rem;
  font-weight: 700;
}

.category-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 32px;
  flex-wrap: wrap;
}

.category-tab {
  padding: 10px 20px;
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 8px;
  color: #cbd5e1;
  cursor: pointer;
  transition: all 0.2s;
}

.category-tab:hover {
  border-color: #a78bfa;
}

.category-tab.active {
  background: #7c3aed;
  border-color: #7c3aed;
  color: white;
}

.game-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
  margin-bottom: 48px;
}

.game-card {
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.2s;
}

.game-card:hover {
  transform: translateY(-4px);
  border-color: #7c3aed60;
  box-shadow: 0 10px 30px rgba(124, 58, 237, 0.2);
}

.game-img {
  position: relative;
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.favorite-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 50%;
  background: rgba(15, 23, 42, 0.75);
  color: #cbd5e1;
  cursor: pointer;
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.favorite-btn:hover,
.favorite-btn.active {
  background: #f472b6;
  color: white;
}

.img-placeholder {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.game-info {
  padding: 16px;
}

.game-title {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 8px;
}

.game-tag {
  color: #94a3b8;
  font-size: 0.9rem;
}

.no-games {
  grid-column: 1 / -1;
  text-align: center;
  padding: 40px;
  color: #64748b;
}

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
  padding: 32px;
  width: 90%;
  max-width: 450px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.modal-header h2 {
  font-size: 1.5rem;
}

.close-btn {
  background: none;
  border: none;
  color: #94a3b8;
  font-size: 1.5rem;
  cursor: pointer;
  transition: color 0.2s;
}

.close-btn:hover {
  color: #edf2f7;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #cbd5e1;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 12px 16px;
  background: #0b0e14;
  border: 1px solid #2a3748;
  border-radius: 8px;
  color: #edf2f7;
  font-size: 1rem;
}

.form-group input:focus {
  outline: none;
  border-color: #a78bfa;
}

.error-message {
  color: #ef4444;
  margin-bottom: 16px;
  font-size: 0.9rem;
}

.success-message {
  color: #10b981;
  margin-bottom: 16px;
  font-size: 0.9rem;
}

.submit-btn {
  width: 100%;
  padding: 12px;
  background: linear-gradient(145deg, #7c3aed, #a78bfa);
  border: none;
  border-radius: 8px;
  color: white;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 14px rgba(124, 58, 237, 0.4);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.modal-footer {
  margin-top: 20px;
  text-align: center;
  color: #94a3b8;
}

.modal-footer a {
  color: #a78bfa;
  text-decoration: none;
}

.modal-footer a:hover {
  text-decoration: underline;
}

.history-panel-overlay {
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

.history-panel {
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 16px;
  width: 90%;
  max-width: 500px;
  max-height: 70vh;
  overflow: hidden;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #2a3748;
}

.panel-body {
  padding: 24px;
  max-height: calc(70vh - 80px);
  overflow-y: auto;
}

.empty-history {
  text-align: center;
  color: #64748b;
  padding: 40px;
}

.history-item {
  padding: 12px 0;
  border-bottom: 1px solid #2a3748;
}

.history-item:last-child {
  border-bottom: none;
}

.history-item.clickable {
  cursor: pointer;
}

.history-item.clickable:hover {
  background: rgba(124, 58, 237, 0.08);
}

.vip-lock-tag {
  margin-left: 8px;
  font-size: 0.75rem;
  color: #f472b6;
}

.history-limit-hint {
  font-size: 0.85rem;
  color: #94a3b8;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #2a3748;
}

.panel-footer {
  padding: 12px 20px;
  border-top: 1px solid #2a3748;
  text-align: right;
}

.clear-history-btn {
  background: transparent;
  border: 1px solid #475569;
  color: #94a3b8;
  padding: 6px 14px;
  border-radius: 8px;
  cursor: pointer;
}

.clear-history-btn:hover {
  color: #f87171;
  border-color: #f87171;
}

.history-name {
  font-weight: 500;
  margin-bottom: 4px;
}

.history-time {
  color: #64748b;
  font-size: 0.85rem;
}
</style>
