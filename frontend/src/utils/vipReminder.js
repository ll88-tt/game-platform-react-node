/** 会员到期前多少天内开始提醒 */
export const VIP_REMINDER_DAYS = 10

/**
 * 是否应展示 VIP 到期提醒（登录弹窗 / 服务页横幅）
 */
export function shouldShowVipReminder(userStore) {
  if (!userStore.isLoggedIn || userStore.isAdmin || userStore.isVipLifetime) {
    return false
  }

  if (userStore.vipExpired) {
    return true
  }

  if (userStore.vipActive && userStore.vipDaysRemaining != null) {
    return userStore.vipDaysRemaining <= VIP_REMINDER_DAYS
  }

  return false
}

/**
 * 生成 VIP 到期提醒文案
 */
export function getVipReminderMessage(userStore) {
  if (!shouldShowVipReminder(userStore)) {
    return null
  }

  if (userStore.vipExpired) {
    return '您的 VIP 会员已过期，续费后可继续畅玩专属游戏'
  }

  if (userStore.vipDaysRemaining === 0) {
    return '您的 VIP 会员今日到期，请及时续费'
  }

  return `您的 VIP 会员还有 ${userStore.vipDaysRemaining} 天到期`
}

/**
 * 进入服务页：同一会话内仅弹窗提示一次
 */
export function showVipReminderOnce(userStore, context = 'page') {
  const message = getVipReminderMessage(userStore)
  if (!message) return

  const key = `vipReminder_${context}_${userStore.currentUser}`
  if (sessionStorage.getItem(key)) return

  sessionStorage.setItem(key, '1')
  alert(message)
}

/**
 * 登录成功：若处于提醒窗口内则弹窗提示
 */
export function showVipReminderOnLogin(userStore) {
  const message = getVipReminderMessage(userStore)
  if (!message) return
  alert(`登录成功！${message}`)
}
