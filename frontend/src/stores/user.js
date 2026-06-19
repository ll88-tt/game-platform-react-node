import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'

export const useUserStore = defineStore('user', () => {
    const isLoggedIn = ref(false)
    const currentUser = ref(null)
    const isAdmin = ref(false)
    const isVip = ref(false)
    const vipExpiryTime = ref(null)
    const permissionLevel = ref('FREE')
    const vipActive = ref(false)
    const vipDaysRemaining = ref(null)
    const vipExpired = ref(false)
    const isVipLifetime = ref(false)

    const hasValidPermission = computed(() => {
        return isAdmin.value || vipActive.value
    })

    function applyUserData(data) {
        isLoggedIn.value = true
        currentUser.value = data.username
        isAdmin.value = data.isAdmin || false
        isVip.value = data.isVip || false
        vipExpiryTime.value = data.vipExpiryTime ?? null
        vipActive.value = data.vipActive ?? (data.isVip || false)
        vipDaysRemaining.value = data.vipDaysRemaining ?? null
        vipExpired.value = data.vipExpired ?? false
        isVipLifetime.value = data.isVipLifetime ?? false
        permissionLevel.value = determinePermissionLevel(data)
    }

    function resetUser() {
        isLoggedIn.value = false
        currentUser.value = null
        isAdmin.value = false
        isVip.value = false
        vipExpiryTime.value = null
        permissionLevel.value = 'FREE'
        vipActive.value = false
        vipDaysRemaining.value = null
        vipExpired.value = false
        isVipLifetime.value = false
    }

    async function checkLoginStatus() {
        try {
            const response = await axios.get('/api/check-login', {
                withCredentials: true
            })
            const data = response.data

            if (data.loggedIn) {
                applyUserData(data)
            } else {
                resetUser()
            }
        } catch (error) {
            console.error('检查登录状态失败:', error)
            resetUser()
        }
    }

    async function login(username, password) {
        try {
            const response = await axios.post('/api/login', {
                username,
                password
            }, {
                withCredentials: true
            })

            const data = response.data
            if (data.success) {
                applyUserData(data)
                return { success: true, data }
            } else {
                return { success: false, message: data.message }
            }
        } catch (error) {
            return { success: false, message: '网络错误' }
        }
    }

    async function logout() {
        try {
            await axios.post('/api/logout', {}, {
                withCredentials: true
            })
        } catch (error) {
            console.error('登出错误:', error)
        } finally {
            resetUser()
        }
    }

    function determinePermissionLevel(data) {
        if (data.isAdmin || data.isVipLifetime) return 'LIFETIME'
        if (!data.vipActive && !data.isVip) return 'FREE'

        const vipStatus = data.vipStatus || ''
        if (vipStatus.includes('终身') || vipStatus.includes('永久')) {
            return 'LIFETIME'
        }

        if (!data.vipExpiryTime || data.vipExpiryTime === '永久') {
            return 'LIFETIME'
        }

        const now = new Date()
        const expiry = new Date(data.vipExpiryTime)
        const diffMonths = (expiry.getFullYear() - now.getFullYear()) * 12 +
            (expiry.getMonth() - now.getMonth())

        if (diffMonths >= 12) return 'YEARLY'
        if (diffMonths >= 3) return 'QUARTERLY'
        if (diffMonths >= 1) return 'MONTHLY'
        return 'MONTHLY'
    }

    return {
        isLoggedIn,
        currentUser,
        isAdmin,
        isVip,
        vipExpiryTime,
        permissionLevel,
        vipActive,
        vipDaysRemaining,
        vipExpired,
        isVipLifetime,
        hasValidPermission,
        checkLoginStatus,
        login,
        logout
    }
})
