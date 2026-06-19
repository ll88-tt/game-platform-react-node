import axios from 'axios'

export const userApi = {
    changePassword(oldPassword, newPassword) {
        return axios.put('/api/user/password', { oldPassword, newPassword }, { withCredentials: true })
    },

    getLibraryLimits() {
        return axios.get('/api/user/library-limits', { withCredentials: true })
    },

    getFavorites() {
        return axios.get('/api/user/favorites', { withCredentials: true })
    },

    getFavoriteIds() {
        return axios.get('/api/user/favorites/ids', { withCredentials: true })
    },

    addFavorite(gameId) {
        return axios.post(`/api/user/favorites/${gameId}`, {}, { withCredentials: true })
    },

    removeFavorite(gameId) {
        return axios.delete(`/api/user/favorites/${gameId}`, { withCredentials: true })
    },

    getBrowseHistory() {
        return axios.get('/api/user/browse-history', { withCredentials: true })
    },

    recordBrowse(gameId) {
        return axios.post(`/api/user/browse-history/${gameId}`, {}, { withCredentials: true })
    },

    syncBrowseHistory(items) {
        return axios.post('/api/user/browse-history/sync', { items }, { withCredentials: true })
    },

    removeBrowseHistory(gameId) {
        return axios.delete(`/api/user/browse-history/${gameId}`, { withCredentials: true })
    },

    clearBrowseHistory() {
        return axios.delete('/api/user/browse-history', { withCredentials: true })
    }
}
