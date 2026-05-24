import axios from 'axios'

export const feedbackApi = {
    submitFeedback(feedback) {
        return axios.post('/api/feedback', feedback)
    },

    getAllFeedbacks() {
        return axios.get('/api/feedback', { withCredentials: true })
    },

    markAsResolved(id) {
        return axios.put(`/api/feedback/${id}/resolve`)
    },

    deleteFeedback(id) {
        return axios.delete(`/api/feedback/${id}`)
    }
}
