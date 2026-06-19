import axios from 'axios'

export const membershipPlansApi = {
    getEnabledPlans() {
        return axios.get('/api/membership-plans', { withCredentials: true })
    }
}
