import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import AboutView from '../views/AboutView.vue'
import ServiceView from '../views/ServiceView.vue'
import ContactView from '../views/ContactView.vue'
import AdminFeedbackView from '../views/AdminFeedbackView.vue'

const routes = [
    {
        path: '/',
        name: 'home',
        component: HomeView
    },
    {
        path: '/home',
        name: 'home',
        component: HomeView
    },
    {
        path: '/about',
        name: 'about',
        component: AboutView
    },
    {
        path: '/service',
        name: 'service',
        component: ServiceView
    },
    {
        path: '/contact',
        name: 'contact',
        component: ContactView
    },
    {
        path: '/admin-feedback',
        name: 'admin-feedback',
        component: AdminFeedbackView,
        meta: { requiresAdmin: true }
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 路由守卫：检查管理员权限
router.beforeEach(async (to, from, next) => {
    if (to.meta.requiresAdmin) {
        const userStore = useUserStore()
        await userStore.checkLoginStatus()

        if (!userStore.isLoggedIn) {
            alert('请先登录')
            next('/home')
        } else if (!userStore.isAdmin) {
            alert('无权访问：仅管理员可访问此页面')
            next('/home')
        } else {
            next()
        }
    } else {
        next()
    }
})

export default router
