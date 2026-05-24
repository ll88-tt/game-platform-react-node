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

    const hasValidPermission = computed(() => {
        return isAdmin.value || isVip.value
    })

    async function checkLoginStatus() {
        try {
            const response = await axios.get('/api/check-login', {
                withCredentials: true
            })
            const data = response.data

            if (data.loggedIn) {
                isLoggedIn.value = true
                currentUser.value = data.username
                isAdmin.value = data.isAdmin || false
                isVip.value = data.isVip || false
                vipExpiryTime.value = data.vipExpiryTime
                permissionLevel.value = determinePermissionLevel(data)
            } else {
                logout()
            }
        } catch (error) {
            console.error('检查登录状态失败:', error)
            logout()
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
                isLoggedIn.value = true
                currentUser.value = data.username
                isAdmin.value = data.isAdmin || false
                isVip.value = data.isVip || false
                vipExpiryTime.value = data.vipExpiryTime
                permissionLevel.value = determinePermissionLevel(data)
                return { success: true }
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
            isLoggedIn.value = false
            currentUser.value = null
            isAdmin.value = false
            isVip.value = false
            vipExpiryTime.value = null
            permissionLevel.value = 'FREE'
        }
    }

    function determinePermissionLevel(data) {
        if (data.isAdmin) return 'LIFETIME'
        if (!data.isVip) return 'FREE'

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
        return 'FREE'
    }

    return {
        isLoggedIn,
        currentUser,
        isAdmin,
        isVip,
        vipExpiryTime,
        permissionLevel,
        hasValidPermission,
        checkLoginStatus,
        login,
        logout
    }
})
