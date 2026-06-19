import axios from 'axios'

export const adminMembershipPlansApi = {
    getAllPlans() {
        return axios.get('/api/admin/membership-plans', { withCredentials: true })
    },

    createPlan(plan) {
        return axios.post('/api/admin/membership-plans', plan, { withCredentials: true })
    },

    updatePlan(id, plan) {
        return axios.put(`/api/admin/membership-plans/${id}`, plan, { withCredentials: true })
    },

    toggleEnabled(id, enabled) {
        return axios.patch(`/api/admin/membership-plans/${id}/enabled`, { enabled }, { withCredentials: true })
    },

    deletePlan(id) {
        return axios.delete(`/api/admin/membership-plans/${id}`, { withCredentials: true })
    }
}
