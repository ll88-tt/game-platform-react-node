<template>
  <div class="contact-container">
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
        <router-link to="/contact" class="active">联系</router-link>

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
        <i class="fas fa-envelope"></i> 联系我们
      </h1>
      <p class="page-subtitle">有问题或建议？我们随时为您提供帮助</p>
    </section>

    <!-- 联系方式 -->
    <section class="contact-methods">
      <div class="section-header">
        <h2><i class="fas fa-address-book"></i> 联系方式</h2>
        <p>通过以下方式与我们取得联系</p>
      </div>

      <div class="methods-grid">
        <div class="method-card">
          <div class="method-icon qq">
            <i class="fab fa-qq"></i>
          </div>
          <h3>QQ</h3>
          <p class="method-value">3531316256</p>
          <p class="method-desc">工作日 9:00-18:00 在线</p>
          <a href="tencent://message/?uin=123456789" class="contact-link">
            <i class="fas fa-comments"></i> 发起会话
          </a>
        </div>

        <div class="method-card">
          <div class="method-icon wechat">
            <i class="fab fa-weixin"></i>
          </div>
          <h3>微信</h3>
          <p class="method-value">F13692004TN</p>
          <p class="method-desc">扫码添加客服微信</p>
          <div class="qr-code-placeholder">
            <i class="fas fa-qrcode"></i>
            <span>二维码</span>
          </div>
        </div>

        <div class="method-card">
          <div class="method-icon phone">
            <i class="fas fa-phone-alt"></i>
          </div>
          <h3>电话</h3>
          <p class="method-value">134-2207-0336</p>
          <p class="method-desc">周一至周五 9:00-18:00</p>
          <a href="tel:400-123-4567" class="contact-link">
            <i class="fas fa-phone"></i> 立即拨打
          </a>
        </div>

        <div class="method-card">
          <div class="method-icon email">
            <i class="fas fa-envelope"></i>
          </div>
          <h3>邮箱</h3>
          <p class="method-value">3531316256@qq.com</p>
          <p class="method-desc">24小时内回复</p>
          <a href="mailto:support@gamevila.com" class="contact-link">
            <i class="fas fa-paper-plane"></i> 发送邮件
          </a>
        </div>
      </div>
    </section>

    <!-- 问题反馈表单 -->
    <section class="feedback-section">
      <div class="section-header">
        <h2><i class="fas fa-bug"></i> 问题反馈</h2>
        <p>遇到什么问题？请详细描述，我们会尽快解决</p>
      </div>

      <div class="feedback-form-container">
        <form @submit.prevent="handleSubmitFeedback" class="feedback-form">
          <div class="form-row">
            <div class="form-group">
              <label for="userName">
                <i class="fas fa-user"></i> 您的姓名
              </label>
              <input
                  type="text"
                  id="userName"
                  v-model="feedbackForm.userName"
                  placeholder="选填，方便我们称呼您"
              />
            </div>

            <div class="form-group">
              <label for="userEmail">
                <i class="fas fa-envelope"></i> 联系邮箱
              </label>
              <input
                  type="email"
                  id="userEmail"
                  v-model="feedbackForm.userEmail"
                  placeholder="选填，方便我们回复您"
              />
            </div>
          </div>

          <div class="form-group">
            <label for="problemType">
              <i class="fas fa-tag"></i> 问题类型 <span class="required">*</span>
            </label>
            <select id="problemType" v-model="feedbackForm.problemType" required>
              <option value="">请选择问题类型</option>
              <option value="login">登录/注册问题</option>
              <option value="payment">支付/订阅问题</option>
              <option value="game">游戏加载问题</option>
              <option value="search">搜索功能问题</option>
              <option value="ui">界面显示问题</option>
              <option value="performance">性能问题</option>
              <option value="bug">程序错误</option>
              <option value="suggestion">功能建议</option>
              <option value="other">其他问题</option>
            </select>
          </div>

          <div class="form-group">
            <label for="problemDesc">
              <i class="fas fa-align-left"></i> 问题描述 <span class="required">*</span>
            </label>
            <textarea
                id="problemDesc"
                v-model="feedbackForm.problemDesc"
                rows="6"
                required
                placeholder="请详细描述您遇到的问题，包括：&#10;1. 问题发生的具体场景&#10;2. 出现的错误提示&#10;3. 您期望的结果&#10;4. 其他相关信息"
            ></textarea>
          </div>

          <div class="form-info">
            <i class="fas fa-info-circle"></i>
            <span>我们会自动收集您的浏览器信息，以便更好地定位问题</span>
          </div>

          <div v-if="submitError" class="error-message">
            <i class="fas fa-exclamation-circle"></i>
            {{ submitError }}
          </div>

          <div v-if="submitSuccess" class="success-message">
            <i class="fas fa-check-circle"></i>
            {{ submitSuccess }}
          </div>

          <button type="submit" class="submit-btn" :disabled="submitting">
            <i v-if="submitting" class="fas fa-spinner fa-spin"></i>
            <i v-else class="fas fa-paper-plane"></i>
            {{ submitting ? '提交中...' : '提交反馈' }}
          </button>
        </form>
      </div>
    </section>

    <!-- 常见问题快速链接 -->
    <section class="quick-help">
      <div class="section-header">
        <h2><i class="fas fa-life-ring"></i> 快速帮助</h2>
        <p>这些问题可能已经有人问过，点击查看解答</p>
      </div>

      <div class="help-links">
        <router-link to="/about" class="help-link">
          <i class="fas fa-question-circle"></i>
          <span>关于项目与技术栈</span>
        </router-link>

        <router-link to="/service" class="help-link">
          <i class="fas fa-crown"></i>
          <span>VIP 订阅说明</span>
        </router-link>

        <a href="#" class="help-link" @click.prevent="showComingSoon">
          <i class="fas fa-book"></i>
          <span>使用教程与指南</span>
        </a>

        <a href="#" class="help-link" @click.prevent="showComingSoon">
          <i class="fas fa-shield-alt"></i>
          <span>隐私政策与条款</span>
        </a>
      </div>
    </section>

    <!-- 提交成功弹窗 -->
    <div v-if="showSuccessModal" class="modal-overlay" @click="closeSuccessModal">
      <div class="modal" @click.stop>
        <div class="modal-icon">
          <i class="fas fa-check-circle"></i>
        </div>

        <h2>反馈提交成功！</h2>
        <p>感谢您的反馈，我们会尽快处理并回复您</p>

        <div class="modal-footer">
          <button class="btn-primary" @click="closeSuccessModal">
            知道了
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
const submitting = ref(false)
const submitError = ref('')
const submitSuccess = ref('')
const showSuccessModal = ref(false)

const feedbackForm = ref({
  userName: '',
  userEmail: '',
  problemType: '',
  problemDesc: ''
})

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

  // 如果已登录，自动填充用户名和邮箱
  if (userStore.isLoggedIn) {
    feedbackForm.value.userName = userStore.currentUser
  }
})

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
  router.push('/admin-feedback')
}

function openHistoryPanel() {
  showDropdown.value = false
  if (userStore.isLoggedIn) {
    router.push({ path: '/user-center', query: { tab: 'history' } })
  } else {
    router.push({ path: '/home', query: { history: '1' } })
  }
}

async function handleSubmitFeedback() {
  if (!feedbackForm.value.problemType || !feedbackForm.value.problemDesc) {
    submitError.value = '请填写必填字段'
    return
  }

  submitting.value = true
  submitError.value = ''
  submitSuccess.value = ''

  try {
    // 获取浏览器信息
    const browserInfo = navigator.userAgent

    const response = await feedbackApi.submitFeedback({
      userName: feedbackForm.value.userName || null,
      userEmail: feedbackForm.value.userEmail || null,
      problemType: feedbackForm.value.problemType,
      problemDesc: feedbackForm.value.problemDesc,
      browserInfo: browserInfo
    })

    if (response.data.success) {
      submitSuccess.value = '反馈提交成功！'
      showSuccessModal.value = true

      // 重置表单
      feedbackForm.value = {
        userName: userStore.isLoggedIn ? userStore.currentUser : '',
        userEmail: '',
        problemType: '',
        problemDesc: ''
      }
    } else {
      submitError.value = response.data.message || '提交失败'
    }
  } catch (error) {
    console.error('提交反馈失败:', error)
    submitError.value = '网络错误，请稍后重试'
  } finally {
    submitting.value = false
  }
}

function closeSuccessModal() {
  showSuccessModal.value = false
  submitSuccess.value = ''
}

function showComingSoon() {
  alert('功能开发中，敬请期待！')
}
</script>

<style scoped>
.contact-container {
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

/* 联系方式 */
.contact-methods {
  margin-bottom: 64px;
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

.methods-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 24px;
}

.method-card {
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 16px;
  padding: 32px;
  text-align: center;
  transition: all 0.3s;
}

.method-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.3);
  border-color: #a78bfa;
}

.method-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2.5rem;
}

.method-icon.qq {
  background: linear-gradient(135deg, #12b7f520, #12b7f510);
  color: #12b7f5;
}

.method-icon.wechat {
  background: linear-gradient(135deg, #07c16020, #07c16010);
  color: #07c160;
}

.method-icon.phone {
  background: linear-gradient(135deg, #34d39920, #10b98110);
  color: #34d399;
}

.method-icon.email {
  background: linear-gradient(135deg, #a78bfa20, #7c3aed10);
  color: #a78bfa;
}

.method-card h3 {
  font-size: 1.3rem;
  margin-bottom: 12px;
}

.method-value {
  font-size: 1.1rem;
  font-weight: 600;
  color: #edf2f7;
  margin-bottom: 8px;
}

.method-desc {
  color: #94a3b8;
  font-size: 0.9rem;
  margin-bottom: 20px;
}

.contact-link {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: linear-gradient(135deg, #7c3aed, #a78bfa);
  color: white;
  text-decoration: none;
  border-radius: 8px;
  font-weight: 600;
  transition: all 0.2s;
}

.contact-link:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 14px rgba(124, 58, 237, 0.4);
}

.qr-code-placeholder {
  width: 120px;
  height: 120px;
  margin: 20px auto 0;
  background: #0b0e14;
  border: 2px dashed #2a3748;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #64748b;
}

.qr-code-placeholder i {
  font-size: 2.5rem;
  margin-bottom: 8px;
}

.qr-code-placeholder span {
  font-size: 0.85rem;
}

/* 反馈表单 */
.feedback-section {
  margin-bottom: 64px;
}

.feedback-form-container {
  max-width: 800px;
  margin: 0 auto;
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 16px;
  padding: 48px;
}

.feedback-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  color: #cbd5e1;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 8px;
}

.required {
  color: #ef4444;
}

.form-group input,
.form-group select,
.form-group textarea {
  padding: 12px 16px;
  background: #0b0e14;
  border: 1px solid #2a3748;
  border-radius: 8px;
  color: #edf2f7;
  font-size: 1rem;
  font-family: inherit;
  transition: border-color 0.2s;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #a78bfa;
}

.form-group textarea {
  resize: vertical;
  line-height: 1.6;
}

.form-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #7c3aed10;
  border: 1px solid #7c3aed30;
  border-radius: 8px;
  color: #a78bfa;
  font-size: 0.9rem;
}

.error-message {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #ef444410;
  border: 1px solid #ef444430;
  border-radius: 8px;
  color: #ef4444;
}

.success-message {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #10b98110;
  border: 1px solid #10b98130;
  border-radius: 8px;
  color: #10b981;
}

.submit-btn {
  padding: 14px 32px;
  background: linear-gradient(135deg, #7c3aed, #a78bfa);
  border: none;
  border-radius: 12px;
  color: white;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(124, 58, 237, 0.4);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 快速帮助 */
.quick-help {
  margin-bottom: 64px;
}

.help-links {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
  max-width: 800px;
  margin: 0 auto;
}

.help-link {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 24px;
  background: #161e2a;
  border: 1px solid #2a3748;
  border-radius: 12px;
  color: #cbd5e1;
  text-decoration: none;
  transition: all 0.2s;
}

.help-link:hover {
  border-color: #a78bfa;
  transform: translateX(4px);
}

.help-link i {
  font-size: 1.5rem;
  color: #a78bfa;
}

.help-link span {
  font-weight: 500;
}

/* 成功弹窗 */
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
  color: #10b981;
  margin-bottom: 24px;
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
  justify-content: center;
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

/* 响应式 */
@media (max-width: 768px) {
  .form-row {
    grid-template-columns: 1fr;
  }

  .feedback-form-container {
    padding: 32px 24px;
  }
}
</style>
