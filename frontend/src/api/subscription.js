import axios from 'axios'

export const subscriptionApi = {
    getSubscription() {
        return axios.get('/api/subscription', { withCredentials: true })
    },

    getOrders() {
        return axios.get('/api/orders', { withCredentials: true })
    },

    createPayment(planCode) {
        return axios.post('/api/payment/create', { plan: planCode }, { withCredentials: true })
    },

    confirmPayment(orderId) {
        return axios.post('/api/payment/confirm', { orderId }, { withCredentials: true })
    }
}
