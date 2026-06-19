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

        <router-link to="/admin-dashboard" class="admin-btn dashboard-btn">
          <i class="fas fa-chart-pie"></i> 仪表盘
        </router-link>

        <router-link to="/admin-games" class="active admin-btn games-admin-btn">
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
          <i :class="activeTab === 'games' ? 'fas fa-database' : 'fas fa-tags'"></i>
          {{ activeTab === 'games' ? '游戏管理后台' : '会员定价管理' }}
        </h1>
        <p class="page-subtitle">
          {{ activeTab === 'games'
              ? '增删改查游戏、上下架、设置 VIP/免费、上传封面'
              : '管理服务页会员套餐的售价、原价与展示信息' }}
        </p>
      </section>

      <section class="admin-tabs">
        <button
            class="tab-btn"
            :class="{ active: activeTab === 'games' }"
            @click="switchTab('games')"
        >
          <i class="fas fa-gamepad"></i> 游戏管理
        </button>
        <button
            class="tab-btn"
            :class="{ active: activeTab === 'membership' }"
            @click="switchTab('membership')"
        >
          <i class="fas fa-crown"></i> 会员定价
        </button>
      </section>

      <div v-if="activeTab === 'games'">
      <section class="stats-section">
        <div class="stat-card total">
          <div class="stat-icon"><i class="fas fa-gamepad"></i></div>
          <div class="stat-info">
            <div class="stat-value">{{ totalCount }}</div>
            <div class="stat-label">游戏总数</div>
          </div>
        </div>
        <div class="stat-card published">
          <div class="stat-icon"><i class="fas fa-eye"></i></div>
          <div class="stat-info">
            <div class="stat-value">{{ publishedCount }}</div>
            <div class="stat-label">已上架</div>
          </div>
        </div>
        <div class="stat-card unpublished">
          <div class="stat-icon"><i class="fas fa-eye-slash"></i></div>
          <div class="stat-info">
            <div class="stat-value">{{ unpublishedCount }}</div>
            <div class="stat-label">已下架</div>
          </div>
        </div>
        <div class="stat-card vip">
          <div class="stat-icon"><i class="fas fa-crown"></i></div>
          <div class="stat-info">
            <div class="stat-value">{{ vipCount }}</div>
            <div class="stat-label">VIP 游戏</div>
          </div>
        </div>
      </section>

      <section class="toolbar">
        <input
            v-model="searchKeyword"
            type="text"
            class="search-input"
            placeholder="搜索游戏名称、分类..."
        />

        <div class="filter-bar">
          <button
              v-for="item in filterOptions"
              :key="item.value"
              class="filter-btn"
              :class="{ active: currentFilter === item.value }"
              @click="currentFilter = item.value"
          >
            {{ item.label }}
          </button>
        </div>

        <button class="btn-primary" @click="openCreateModal">
          <i class="fas fa-plus"></i> 新增游戏
        </button>

        <button class="refresh-btn" @click="loadGames">
          <i class="fas fa-sync-alt"></i> 刷新
        </button>
      </section>

      <section class="table-section">
        <div v-if="loading" class="loading-state">
          <i class="fas fa-spinner fa-spin"></i>
          <p>加载中...</p>
        </div>

        <div v-else-if="filteredGames.length === 0" class="empty-state">
          <i class="fas fa-box-open"></i>
          <p>暂无游戏数据</p>
        </div>

        <div v-else class="table-wrapper">
          <table class="games-table">
            <thead>
              <tr>
                <th>封面</th>
                <th>名称</th>
                <th>分类</th>
                <th>类型</th>
                <th>状态</th>
                <th>搜索次数</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="game in filteredGames" :key="game.id">
                <td>
                  <div class="cover-cell">
                    <img
                        v-if="game.imageUrl"
                        :src="game.imageUrl"
                        :alt="game.name"
                        class="cover-img"
                    />
                    <div v-else class="cover-placeholder">
                      <i class="fas fa-image"></i>
                    </div>
                  </div>
                </td>
                <td>
                  <div class="game-name">{{ game.name }}</div>
                  <div class="game-desc">{{ game.description || '暂无简介' }}</div>
                </td>
                <td>{{ game.category || '-' }}</td>
                <td>
                  <span class="badge" :class="game.vipOnly ? 'vip' : 'free'">
                    {{ game.vipOnly ? 'VIP' : '免费' }}
                  </span>
                </td>
                <td>
                  <span class="badge" :class="game.published ? 'published' : 'unpublished'">
                    {{ game.published ? '已上架' : '已下架' }}
                  </span>
                </td>
                <td>{{ game.searchCount ?? 0 }}</td>
                <td>
                  <div class="action-btns">
                    <button class="action-btn edit-btn" title="编辑" @click="openEditModal(game)">
                      <i class="fas fa-edit"></i>
                    </button>
                    <button
                        class="action-btn"
                        :class="game.published ? 'warn-btn' : 'success-btn'"
                        :title="game.published ? '下架' : '上架'"
                        @click="handleTogglePublish(game)"
                    >
                      <i :class="game.published ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
                    </button>
                    <button
                        class="action-btn"
                        :class="game.vipOnly ? 'free-btn' : 'vip-btn'"
                        :title="game.vipOnly ? '设为免费' : '设为VIP'"
                        @click="handleToggleVip(game)"
                    >
                      <i :class="game.vipOnly ? 'fas fa-unlock' : 'fas fa-crown'"></i>
                    </button>
                    <button class="action-btn delete-btn" title="删除" @click="confirmDelete(game.id)">
                      <i class="fas fa-trash"></i>
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>
      </div>

      <div v-if="activeTab === 'membership'">
        <section class="toolbar">
          <button class="btn-primary" @click="openPlanCreateModal">
            <i class="fas fa-plus"></i> 新增套餐
          </button>
          <button class="refresh-btn" @click="loadMembershipPlans">
            <i class="fas fa-sync-alt"></i> 刷新
          </button>
        </section>

        <section class="table-section">
          <div v-if="planLoading" class="loading-state">
            <i class="fas fa-spinner fa-spin"></i>
            <p>加载中...</p>
          </div>

          <div v-else-if="membershipPlans.length === 0" class="empty-state">
            <i class="fas fa-tags"></i>
            <p>暂无会员套餐，点击「新增套餐」创建</p>
          </div>

          <div v-else class="table-wrapper">
            <table class="games-table">
              <thead>
                <tr>
                  <th>名称</th>
                  <th>套餐代码</th>
                  <th>售价</th>
                  <th>原价</th>
                  <th>周期</th>
                  <th>优惠说明</th>
                  <th>推荐</th>
                  <th>状态</th>
                  <th>排序</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="plan in membershipPlans" :key="plan.id">
                  <td><span class="game-name">{{ plan.name }}</span></td>
                  <td><code class="plan-code">{{ plan.planCode }}</code></td>
                  <td><span class="price-text">¥{{ formatPrice(plan.price) }}</span></td>
                  <td>
                    <span v-if="plan.originalPrice" class="original-price-text">¥{{ formatPrice(plan.originalPrice) }}</span>
                    <span v-else class="text-muted">-</span>
                  </td>
                  <td>{{ plan.periodLabel || '-' }}</td>
                  <td><span class="game-desc">{{ plan.computedSavingsText || plan.savingsText || '-' }}</span></td>
                  <td>
                    <span class="badge" :class="plan.recommended ? 'vip' : 'free'">
                      {{ plan.recommended ? '推荐' : '普通' }}
                    </span>
                  </td>
                  <td>
                    <span class="badge" :class="plan.enabled ? 'published' : 'unpublished'">
                      {{ plan.enabled ? '启用' : '停用' }}
                    </span>
                  </td>
                  <td>{{ plan.sortOrder }}</td>
                  <td>
                    <div class="action-btns">
                      <button class="action-btn edit-btn" title="编辑" @click="openPlanEditModal(plan)">
                        <i class="fas fa-edit"></i>
                      </button>
                      <button
                          class="action-btn"
                          :class="plan.enabled ? 'warn-btn' : 'success-btn'"
                          :title="plan.enabled ? '停用' : '启用'"
                          @click="handleTogglePlanEnabled(plan)"
                      >
                        <i :class="plan.enabled ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
                      </button>
                      <button class="action-btn delete-btn" title="删除" @click="confirmPlanDelete(plan.id)">
                        <i class="fas fa-trash"></i>
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>
      </div>
    </div>

    <!-- 会员套餐表单 -->
    <div v-if="showPlanFormModal" class="modal-overlay" @click="closePlanFormModal">
      <div class="modal form-modal" @click.stop>
        <div class="modal-header">
          <h2>{{ isPlanEditing ? '编辑会员套餐' : '新增会员套餐' }}</h2>
          <button class="close-btn" @click="closePlanFormModal">
            <i class="fas fa-times"></i>
          </button>
        </div>

        <div class="modal-body">
          <div class="form-group">
            <label>套餐代码 *</label>
            <select v-model="planForm.planCode" :disabled="isPlanEditing">
              <option value="">请选择</option>
              <option v-for="code in planCodeOptions" :key="code.value" :value="code.value">
                {{ code.label }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>套餐名称 *</label>
            <input v-model="planForm.name" type="text" placeholder="如：月度会员" />
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>售价 (元) *</label>
              <input v-model.number="planForm.price" type="number" min="0.01" step="0.01" />
            </div>
            <div class="form-group">
              <label>原价 (元)</label>
              <input v-model.number="planForm.originalPrice" type="number" min="0.01" step="0.01" placeholder="可选" />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>周期标签</label>
              <input v-model="planForm.periodLabel" type="text" placeholder="如：/月、/季、一次性" />
            </div>
            <div class="form-group">
              <label>排序</label>
              <input v-model.number="planForm.sortOrder" type="number" min="0" />
            </div>
          </div>

          <div class="form-group">
            <label>优惠说明</label>
            <input v-model="planForm.savingsText" type="text" placeholder="如：省 ¥8（相当于 ¥26/月）" />
          </div>

          <div class="form-row">
            <label class="checkbox-label">
              <input v-model="planForm.recommended" type="checkbox" />
              设为推荐套餐
            </label>
            <label class="checkbox-label">
              <input v-model="planForm.enabled" type="checkbox" />
              启用展示
            </label>
          </div>

          <p v-if="planFormError" class="form-error">{{ planFormError }}</p>
        </div>

        <div class="modal-footer">
          <button class="btn-secondary" @click="closePlanFormModal">取消</button>
          <button class="btn-primary" :disabled="planSaving" @click="submitPlanForm">
            <i v-if="planSaving" class="fas fa-spinner fa-spin"></i>
            {{ planSaving ? '保存中...' : (isPlanEditing ? '保存修改' : '创建套餐') }}
          </button>
        </div>
      </div>
    </div>

    <!-- 会员套餐删除确认 -->
    <div v-if="showPlanDeleteConfirm" class="modal-overlay" @click="closePlanDeleteConfirm">
      <div class="modal" @click.stop>
        <div class="modal-icon warning">
          <i class="fas fa-exclamation-triangle"></i>
        </div>
        <h2>确认删除</h2>
        <p>确定删除此会员套餐？服务页将不再展示该方案。</p>
        <div class="modal-footer">
          <button class="btn-secondary" @click="closePlanDeleteConfirm">取消</button>
          <button class="btn-danger" @click="executePlanDelete">
            <i class="fas fa-trash"></i> 确认删除
          </button>
        </div>
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <div v-if="showFormModal" class="modal-overlay" @click="closeFormModal">
      <div class="modal form-modal" @click.stop>
        <div class="modal-header">
          <h2>{{ isEditing ? '编辑游戏' : '新增游戏' }}</h2>
          <button class="close-btn" @click="closeFormModal">
            <i class="fas fa-times"></i>
          </button>
        </div>

        <div class="modal-body">
          <div class="form-group">
            <label>游戏名称 *</label>
            <input v-model="form.name" type="text" placeholder="请输入游戏名称" />
          </div>

          <div class="form-group">
            <label>游戏链接 *</label>
            <input v-model="form.link" type="url" placeholder="https://..." />
          </div>

          <div class="form-group">
            <label>分类</label>
            <select v-model="form.category">
              <option value="">请选择分类</option>
              <option v-for="cat in categories" :key="cat" :value="cat">{{ cat }}</option>
            </select>
          </div>

          <div class="form-group">
            <label>简介</label>
            <textarea v-model="form.description" rows="3" placeholder="游戏简介"></textarea>
          </div>

          <div class="form-group">
            <label>封面</label>
            <div class="cover-upload">
              <div class="cover-preview">
                <img v-if="form.imageUrl" :src="form.imageUrl" alt="封面预览" />
                <div v-else class="cover-preview-empty">
                  <i class="fas fa-image"></i>
                  <span>暂无封面</span>
                </div>
              </div>
              <div class="cover-upload-actions">
                <label class="upload-btn">
                  <i class="fas fa-upload"></i>
                  {{ uploading ? '上传中...' : '上传封面' }}
                  <input
                      type="file"
                      accept="image/jpeg,image/png,image/webp,image/gif"
                      :disabled="uploading"
                      @change="handleCoverUpload"
                  />
                </label>
                <input
                    v-model="form.imageUrl"
                    type="text"
                    class="url-input"
                    placeholder="或输入图片 URL"
                />
              </div>
            </div>
          </div>

          <div class="form-row">
            <label class="checkbox-label">
              <input v-model="form.vipOnly" type="checkbox" />
              VIP 专属游戏
            </label>
            <label class="checkbox-label">
              <input v-model="form.published" type="checkbox" />
              立即上架
            </label>
          </div>

          <p v-if="formError" class="form-error">{{ formError }}</p>
        </div>

        <div class="modal-footer">
          <button class="btn-secondary" @click="closeFormModal">取消</button>
          <button class="btn-primary" :disabled="saving" @click="submitForm">
            <i v-if="saving" class="fas fa-spinner fa-spin"></i>
            {{ saving ? '保存中...' : (isEditing ? '保存修改' : '创建游戏') }}
          </button>
        </div>
      </div>
    </div>

    <!-- 删除确认 -->
    <div v-if="showDeleteConfirm" class="modal-overlay" @click="closeDeleteConfirm">
      <div class="modal" @click.stop>
        <div class="modal-icon warning">
          <i class="fas fa-exclamation-triangle"></i>
        </div>
        <h2>确认删除</h2>
        <p>确定删除此游戏？此操作不可恢复！</p>
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
import { adminGamesApi } from '../api/adminGames'
import { adminMembershipPlansApi } from '../api/adminMembershipPlans'

const router = useRouter()
const userStore = useUserStore()

const showDropdown = ref(false)
const checkingPermission = ref(true)
const hasPermission = ref(false)
const loading = ref(false)
const saving = ref(false)
const uploading = ref(false)
const allGames = ref([])
const searchKeyword = ref('')
const currentFilter = ref('all')
const showFormModal = ref(false)
const showDeleteConfirm = ref(false)
const isEditing = ref(false)
const editingId = ref(null)
const deleteId = ref(null)
const formError = ref('')

const activeTab = ref('games')
const planLoading = ref(false)
const planSaving = ref(false)
const membershipPlans = ref([])
const showPlanFormModal = ref(false)
const showPlanDeleteConfirm = ref(false)
const isPlanEditing = ref(false)
const editingPlanId = ref(null)
const deletePlanId = ref(null)
const planFormError = ref('')

const planCodeOptions = [
  { value: 'monthly', label: 'monthly - 月度会员' },
  { value: 'quarterly', label: 'quarterly - 季度会员' },
  { value: 'yearly', label: 'yearly - 年度会员' },
  { value: 'lifetime', label: 'lifetime - 终身会员' }
]

const emptyPlanForm = () => ({
  planCode: '',
  name: '',
  price: null,
  originalPrice: null,
  periodLabel: '',
  savingsText: '',
  recommended: false,
  enabled: true,
  sortOrder: 0
})

const planForm = ref(emptyPlanForm())

const categories = ['冒险', 'RPG', '竞速', '解谜', '射击', '模拟', '策略', '休闲', '卡牌', '格斗']

const filterOptions = [
  { value: 'all', label: '全部' },
  { value: 'published', label: '已上架' },
  { value: 'unpublished', label: '已下架' },
  { value: 'vip', label: 'VIP' },
  { value: 'free', label: '免费' }
]

const emptyForm = () => ({
  name: '',
  link: '',
  description: '',
  imageUrl: '',
  category: '',
  vipOnly: false,
  published: true
})

const form = ref(emptyForm())

const totalCount = computed(() => allGames.value.length)
const publishedCount = computed(() => allGames.value.filter(g => g.published).length)
const unpublishedCount = computed(() => allGames.value.filter(g => !g.published).length)
const vipCount = computed(() => allGames.value.filter(g => g.vipOnly).length)

const filteredGames = computed(() => {
  let list = allGames.value

  switch (currentFilter.value) {
    case 'published':
      list = list.filter(g => g.published)
      break
    case 'unpublished':
      list = list.filter(g => !g.published)
      break
    case 'vip':
      list = list.filter(g => g.vipOnly)
      break
    case 'free':
      list = list.filter(g => !g.vipOnly)
      break
  }

  const keyword = searchKeyword.value.trim().toLowerCase()
  if (keyword) {
    list = list.filter(g =>
        (g.name && g.name.toLowerCase().includes(keyword)) ||
        (g.category && g.category.toLowerCase().includes(keyword)) ||
        (g.description && g.description.toLowerCase().includes(keyword))
    )
  }

  return list
})

onMounted(async () => {
  const savedTab = sessionStorage.getItem('adminGamesTab')
  if (savedTab === 'membership') {
    sessionStorage.removeItem('adminGamesTab')
    activeTab.value = 'membership'
  }
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
  await loadGames()
  if (activeTab.value === 'membership') {
    await loadMembershipPlans()
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

async function loadGames() {
  loading.value = true
  try {
    const response = await adminGamesApi.getAllGames()
    allGames.value = response.data
  } catch (error) {
    console.error('加载游戏失败:', error)
    alert(error.response?.status === 403 ? '无权访问' : '加载游戏失败')
  } finally {
    loading.value = false
  }
}

function openCreateModal() {
  isEditing.value = false
  editingId.value = null
  form.value = emptyForm()
  formError.value = ''
  showFormModal.value = true
}

function openEditModal(game) {
  isEditing.value = true
  editingId.value = game.id
  form.value = {
    name: game.name || '',
    link: game.link || '',
    description: game.description || '',
    imageUrl: game.imageUrl || '',
    category: game.category || '',
    vipOnly: !!game.vipOnly,
    published: game.published !== false
  }
  formError.value = ''
  showFormModal.value = true
}

function closeFormModal() {
  showFormModal.value = false
  formError.value = ''
}

async function handleCoverUpload(event) {
  const file = event.target.files?.[0]
  if (!file) return

  uploading.value = true
  formError.value = ''

  try {
    const response = await adminGamesApi.uploadCover(file)
    if (response.data.success) {
      form.value.imageUrl = response.data.url
    } else {
      formError.value = response.data.message || '上传失败'
    }
  } catch (error) {
    console.error('上传封面失败:', error)
    formError.value = error.response?.data?.message || '上传封面失败'
  } finally {
    uploading.value = false
    event.target.value = ''
  }
}

async function submitForm() {
  if (!form.value.name.trim()) {
    formError.value = '游戏名称不能为空'
    return
  }
  if (!form.value.link.trim()) {
    formError.value = '游戏链接不能为空'
    return
  }

  saving.value = true
  formError.value = ''

  const payload = {
    name: form.value.name.trim(),
    link: form.value.link.trim(),
    description: form.value.description?.trim() || '',
    imageUrl: form.value.imageUrl?.trim() || '',
    category: form.value.category || '',
    vipOnly: form.value.vipOnly,
    published: form.value.published
  }

  try {
    const response = isEditing.value
        ? await adminGamesApi.updateGame(editingId.value, payload)
        : await adminGamesApi.createGame(payload)

    if (response.data.success) {
      closeFormModal()
      await loadGames()
    } else {
      formError.value = response.data.message || '保存失败'
    }
  } catch (error) {
    console.error('保存失败:', error)
    formError.value = error.response?.data?.message || '保存失败'
  } finally {
    saving.value = false
  }
}

async function handleTogglePublish(game) {
  const newStatus = !game.published
  const action = newStatus ? '上架' : '下架'
  if (!confirm(`确定${action}「${game.name}」？`)) return

  try {
    const response = await adminGamesApi.togglePublish(game.id, newStatus)
    if (response.data.success) {
      await loadGames()
    } else {
      alert(response.data.message || '操作失败')
    }
  } catch (error) {
    console.error('上下架失败:', error)
    alert('操作失败')
  }
}

async function handleToggleVip(game) {
  const newStatus = !game.vipOnly
  const action = newStatus ? '设为 VIP 专属' : '设为免费'
  if (!confirm(`确定将「${game.name}」${action}？`)) return

  try {
    const response = await adminGamesApi.toggleVip(game.id, newStatus)
    if (response.data.success) {
      await loadGames()
    } else {
      alert(response.data.message || '操作失败')
    }
  } catch (error) {
    console.error('VIP设置失败:', error)
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
    const response = await adminGamesApi.deleteGame(deleteId.value)
    if (response.data.success) {
      closeDeleteConfirm()
      await loadGames()
    } else {
      alert(response.data.message || '删除失败')
    }
  } catch (error) {
    console.error('删除失败:', error)
    alert('删除失败')
  }
}

function switchTab(tab) {
  activeTab.value = tab
  if (tab === 'membership' && membershipPlans.value.length === 0) {
    loadMembershipPlans()
  }
}

function formatPrice(value) {
  if (value == null) return '0'
  const num = Number(value)
  return Number.isInteger(num) ? String(num) : num.toFixed(2)
}

async function loadMembershipPlans() {
  planLoading.value = true
  try {
    const response = await adminMembershipPlansApi.getAllPlans()
    membershipPlans.value = response.data
  } catch (error) {
    console.error('加载会员套餐失败:', error)
    alert(error.response?.status === 403 ? '无权访问' : '加载会员套餐失败')
  } finally {
    planLoading.value = false
  }
}

function openPlanCreateModal() {
  isPlanEditing.value = false
  editingPlanId.value = null
  planForm.value = emptyPlanForm()
  planFormError.value = ''
  showPlanFormModal.value = true
}

function openPlanEditModal(plan) {
  isPlanEditing.value = true
  editingPlanId.value = plan.id
  planForm.value = {
    planCode: plan.planCode || '',
    name: plan.name || '',
    price: plan.price != null ? Number(plan.price) : null,
    originalPrice: plan.originalPrice != null ? Number(plan.originalPrice) : null,
    periodLabel: plan.periodLabel || '',
    savingsText: plan.savingsText || '',
    recommended: !!plan.recommended,
    enabled: plan.enabled !== false,
    sortOrder: plan.sortOrder ?? 0
  }
  planFormError.value = ''
  showPlanFormModal.value = true
}

function closePlanFormModal() {
  showPlanFormModal.value = false
  planFormError.value = ''
}

async function submitPlanForm() {
  if (!planForm.value.planCode) {
    planFormError.value = '请选择套餐代码'
    return
  }
  if (!planForm.value.name.trim()) {
    planFormError.value = '套餐名称不能为空'
    return
  }
  if (!planForm.value.price || planForm.value.price <= 0) {
    planFormError.value = '售价必须大于 0'
    return
  }

  planSaving.value = true
  planFormError.value = ''

  const payload = {
    planCode: planForm.value.planCode,
    name: planForm.value.name.trim(),
    price: planForm.value.price,
    originalPrice: planForm.value.originalPrice || null,
    periodLabel: planForm.value.periodLabel?.trim() || null,
    savingsText: planForm.value.savingsText?.trim() || null,
    recommended: planForm.value.recommended,
    enabled: planForm.value.enabled,
    sortOrder: planForm.value.sortOrder ?? 0
  }

  try {
    const response = isPlanEditing.value
        ? await adminMembershipPlansApi.updatePlan(editingPlanId.value, payload)
        : await adminMembershipPlansApi.createPlan(payload)

    if (response.data.success) {
      closePlanFormModal()
      await loadMembershipPlans()
    } else {
      planFormError.value = response.data.message || '保存失败'
    }
  } catch (error) {
    console.error('保存会员套餐失败:', error)
    planFormError.value = error.response?.data?.message || '保存失败'
  } finally {
    planSaving.value = false
  }
}

async function handleTogglePlanEnabled(plan) {
  const newStatus = !plan.enabled
  const action = newStatus ? '启用' : '停用'
  if (!confirm(`确定${action}「${plan.name}」？`)) return

  try {
    const response = await adminMembershipPlansApi.toggleEnabled(plan.id, newStatus)
    if (response.data.success) {
      await loadMembershipPlans()
    } else {
      alert(response.data.message || '操作失败')
    }
  } catch (error) {
    console.error('切换套餐状态失败:', error)
    alert('操作失败')
  }
}

function confirmPlanDelete(id) {
  deletePlanId.value = id
  showPlanDeleteConfirm.value = true
}

function closePlanDeleteConfirm() {
  showPlanDeleteConfirm.value = false
  deletePlanId.value = null
}

async function executePlanDelete() {
  if (!deletePlanId.value) return

  try {
    const response = await adminMembershipPlansApi.deletePlan(deletePlanId.value)
    if (response.data.success) {
      closePlanDeleteConfirm()
      await loadMembershipPlans()
    } else {
      alert(response.data.message || '删除失败')
    }
  } catch (error) {
    console.error('删除会员套餐失败:', error)
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
  gap: 16px;
  align-items: center;
  flex-wrap: wrap;
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
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.5);
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
.forbidden-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  gap: 24px;
}

.loading-container i { font-size: 3rem; color: #a78bfa; }
.forbidden-container i { font-size: 5rem; color: #ef4444; }
.forbidden-container p { color: #94a3b8; }

.btn-primary {
  padding: 10px 20px;
  background: linear-gradient(135deg, #7c3aed, #a78bfa);
  border: none;
  border-radius: 8px;
  color: white;
  font-weight: 600;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }

.btn-secondary {
  padding: 10px 20px;
  background: #2a3748;
  border: none;
  border-radius: 8px;
  color: #cbd5e1;
  cursor: pointer;
}

.btn-danger {
  padding: 10px 20px;
  background: #ef4444;
  border: none;
  border-radius: 8px;
  color: white;
  cursor: pointer;
}

.page-hero { margin-bottom: 32px; }
.page-title { font-size: 2.2rem; font-weight: 700; margin-bottom: 8px; }
.page-subtitle { color: #94a3b8; }

.admin-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 28px;
}

.tab-btn {
  padding: 10px 20px;
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 10px;
  color: #94a3b8;
  cursor: pointer;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.tab-btn.active {
  background: #7c3aed30;
  border-color: #7c3aed;
  color: #a78bfa;
}

.plan-code {
  background: #0f172a;
  padding: 2px 8px;
  border-radius: 4px;
  color: #a78bfa;
  font-size: 0.85rem;
}

.price-text {
  color: #34d399;
  font-weight: 600;
}

.original-price-text {
  color: #64748b;
  text-decoration: line-through;
}

.text-muted {
  color: #64748b;
}

.stats-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 28px;
}

.stat-card {
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 16px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
}

.stat-card.total .stat-icon { background: #7c3aed30; color: #a78bfa; }
.stat-card.published .stat-icon { background: #10b98130; color: #34d399; }
.stat-card.unpublished .stat-icon { background: #64748b30; color: #94a3b8; }
.stat-card.vip .stat-icon { background: #f472b630; color: #f472b6; }

.stat-value { font-size: 1.8rem; font-weight: 700; }
.stat-label { color: #94a3b8; font-size: 0.9rem; }

.toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
  margin-bottom: 24px;
}

.search-input {
  flex: 1;
  min-width: 200px;
  padding: 10px 16px;
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 8px;
  color: #edf2f7;
}

.filter-bar { display: flex; gap: 8px; flex-wrap: wrap; }

.filter-btn {
  padding: 8px 16px;
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 8px;
  color: #94a3b8;
  cursor: pointer;
}

.filter-btn.active {
  background: #7c3aed30;
  border-color: #7c3aed;
  color: #a78bfa;
}

.refresh-btn {
  padding: 10px 16px;
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 8px;
  color: #cbd5e1;
  cursor: pointer;
}

.table-section {
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 16px;
  overflow: hidden;
}

.loading-state,
.empty-state {
  padding: 60px;
  text-align: center;
  color: #94a3b8;
}

.loading-state i,
.empty-state i {
  font-size: 2.5rem;
  margin-bottom: 12px;
  display: block;
}

.table-wrapper { overflow-x: auto; }

.games-table {
  width: 100%;
  border-collapse: collapse;
}

.games-table th,
.games-table td {
  padding: 14px 16px;
  text-align: left;
  border-bottom: 1px solid #2a3748;
}

.games-table th {
  background: #1e2a3a;
  color: #94a3b8;
  font-weight: 600;
  font-size: 0.85rem;
}

.games-table tr:hover { background: #1e2a3a50; }

.cover-cell { width: 64px; }

.cover-img {
  width: 56px;
  height: 56px;
  object-fit: cover;
  border-radius: 8px;
}

.cover-placeholder {
  width: 56px;
  height: 56px;
  background: #2a3748;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #64748b;
}

.game-name { font-weight: 600; margin-bottom: 4px; }
.game-desc { font-size: 0.85rem; color: #64748b; max-width: 280px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

.badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
}

.badge.vip { background: #f472b630; color: #f472b6; }
.badge.free { background: #10b98130; color: #34d399; }
.badge.published { background: #10b98130; color: #34d399; }
.badge.unpublished { background: #64748b30; color: #94a3b8; }

.action-btns { display: flex; gap: 6px; }

.action-btn {
  width: 34px;
  height: 34px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.edit-btn { background: #6366f1; }
.warn-btn { background: #f59e0b; }
.success-btn { background: #10b981; }
.vip-btn { background: #f472b6; }
.free-btn { background: #34d399; }
.delete-btn { background: #ef4444; }

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  padding: 20px;
}

.modal {
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 16px;
  padding: 28px;
  max-width: 440px;
  width: 100%;
  text-align: center;
}

.form-modal {
  max-width: 560px;
  text-align: left;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.modal-header h2 { font-size: 1.4rem; }

.close-btn {
  background: none;
  border: none;
  color: #94a3b8;
  font-size: 1.2rem;
  cursor: pointer;
}

.modal-body { margin-bottom: 20px; }

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  color: #94a3b8;
  font-size: 0.9rem;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 10px 12px;
  background: #0f172a;
  border: 1px solid #2a3748;
  border-radius: 8px;
  color: #edf2f7;
  box-sizing: border-box;
}

.form-row {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #cbd5e1;
  cursor: pointer;
}

.cover-upload {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.cover-preview {
  width: 120px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  background: #0f172a;
  border: 1px solid #2a3748;
  flex-shrink: 0;
}

.cover-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-preview-empty {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #64748b;
  font-size: 0.8rem;
  gap: 4px;
}

.cover-upload-actions {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.upload-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: #7c3aed30;
  border: 1px dashed #7c3aed;
  border-radius: 8px;
  color: #a78bfa;
  cursor: pointer;
  width: fit-content;
}

.upload-btn input { display: none; }

.url-input { font-size: 0.9rem; }

.form-error {
  color: #ef4444;
  font-size: 0.9rem;
  margin-top: 8px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.modal-icon.warning {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: #f59e0b30;
  color: #f59e0b;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  margin: 0 auto 16px;
}

@media (max-width: 768px) {
  .header { flex-direction: column; gap: 16px; }
  .toolbar { flex-direction: column; align-items: stretch; }
  .cover-upload { flex-direction: column; }
}
</style>
