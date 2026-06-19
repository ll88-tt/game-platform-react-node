import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'
import HomeView from '../views/HomeView.vue'
import AboutView from '../views/AboutView.vue'
import ServiceView from '../views/ServiceView.vue'
import ContactView from '../views/ContactView.vue'
import AdminFeedbackView from '../views/AdminFeedbackView.vue'
import AdminGamesView from '../views/AdminGamesView.vue'
import AdminDashboardView from '../views/AdminDashboardView.vue'
import SubscriptionView from '../views/SubscriptionView.vue'
import UserCenterView from '../views/UserCenterView.vue'

const routes = [

    {
        path: '/',
        name: 'home',
        component: HomeView
    },
    {
        path: '/home',
        name: 'home-page',
        component: HomeView
    },
    {
        path: '/about',
        name: 'about',   // 改成唯一名称 Vue Router 4 中，路由的 name 必须是唯一的
        component: AboutView
    },
    {
        path: '/service',
        name: 'service',
        component: ServiceView
    },
    {
        path: '/subscription',
        name: 'subscription',
        component: SubscriptionView,
        meta: { requiresLogin: true }
    },
    {
        path: '/user-center',
        name: 'user-center',
        component: UserCenterView,
        meta: { requiresLogin: true }
    },
    {
        path: '/contact',
        name: 'contact',
        component: ContactView
    },
    {
        path: '/admin-dashboard',
        name: 'admin-dashboard',
        component: AdminDashboardView,
        meta: { requiresAdmin: true }
    },
    {
        path: '/admin-feedback',
        name: 'admin-feedback',
        component: AdminFeedbackView,
        meta: { requiresAdmin: true }
    },
    {
        path: '/admin-games',
        name: 'admin-games',
        component: AdminGamesView,
        meta: { requiresAdmin: true }
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 路由守卫：检查管理员/登录权限
router.beforeEach(async (to, from, next) => {
    const userStore = useUserStore()

    if (to.meta.requiresAdmin || to.meta.requiresLogin) {
        await userStore.checkLoginStatus()
    }

    if (to.meta.requiresAdmin) {
        if (!userStore.isLoggedIn) {
            alert('请先登录')
            next('/home')
        } else if (!userStore.isAdmin) {
            alert('无权访问：仅管理员可访问此页面')
            next('/home')
        } else {
            next()
        }
    } else if (to.meta.requiresLogin) {
        if (!userStore.isLoggedIn) {
            alert('请先登录')
            next('/home')
        } else {
            next()
        }
    } else {
        next()
    }
})

export default router
