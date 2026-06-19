<template>
  <div
      v-if="visible"
      class="vip-expiry-banner"
      :class="bannerClass"
  >
    <div class="banner-content">
      <i :class="iconClass"></i>
      <span>{{ message }}</span>
      <router-link v-if="showRenewLink" to="/service" class="renew-link">立即续费</router-link>
    </div>
    <button v-if="dismissible" class="close-btn" @click="dismiss">
      <i class="fas fa-times"></i>
    </button>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useUserStore } from '../stores/user'
import {
  VIP_REMINDER_DAYS,
  getVipReminderMessage,
  shouldShowVipReminder
} from '../utils/vipReminder'

const props = defineProps({
  dismissible: { type: Boolean, default: true },
  forceShow: { type: Boolean, default: false }
})

const userStore = useUserStore()
const dismissed = ref(false)

const message = computed(() => getVipReminderMessage(userStore) || '')

const visible = computed(() => {
  if (dismissed.value && !props.forceShow) return false
  return shouldShowVipReminder(userStore)
})

const bannerClass = computed(() => {
  if (userStore.vipExpired) return 'expired'
  if (userStore.vipDaysRemaining != null && userStore.vipDaysRemaining <= VIP_REMINDER_DAYS) {
    return 'warning'
  }
  return 'info'
})

const iconClass = computed(() => {
  if (userStore.vipExpired) return 'fas fa-exclamation-circle'
  if (userStore.vipDaysRemaining != null && userStore.vipDaysRemaining <= VIP_REMINDER_DAYS) {
    return 'fas fa-exclamation-triangle'
  }
  return 'fas fa-crown'
})

const showRenewLink = computed(() => userStore.vipExpired || userStore.vipActive)

function dismiss() {
  dismissed.value = true
}

watch(
    () => userStore.currentUser,
    () => {
      dismissed.value = false
    }
)

defineExpose({ dismiss })
</script>

<style scoped>
.vip-expiry-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 14px 20px;
  border-radius: 12px;
  margin-bottom: 24px;
  border: 1px solid transparent;
}

.vip-expiry-banner.info {
  background: linear-gradient(135deg, #7c3aed20, #a78bfa20);
  border-color: #7c3aed50;
  color: #e9d5ff;
}

.vip-expiry-banner.warning {
  background: linear-gradient(135deg, #f59e0b20, #fbbf2420);
  border-color: #f59e0b60;
  color: #fde68a;
}

.vip-expiry-banner.expired {
  background: linear-gradient(135deg, #ef444420, #f9731620);
  border-color: #ef444460;
  color: #fecaca;
}

.banner-content {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.renew-link {
  color: #a78bfa;
  font-weight: 600;
  text-decoration: none;
  margin-left: 8px;
}

.renew-link:hover {
  text-decoration: underline;
}

.close-btn {
  background: none;
  border: none;
  color: inherit;
  opacity: 0.7;
  cursor: pointer;
  padding: 4px 8px;
  flex-shrink: 0;
}

.close-btn:hover {
  opacity: 1;
}
</style>
