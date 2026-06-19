import axios from 'axios'

export const adminDashboardApi = {
    getStats() {
        return axios.get('/api/admin/dashboard/stats', { withCredentials: true })
    }
}
