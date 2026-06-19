import axios from 'axios'

export const adminGamesApi = {
    getAllGames() {
        return axios.get('/api/admin/games', { withCredentials: true })
    },

    createGame(game) {
        return axios.post('/api/admin/games', game, { withCredentials: true })
    },

    updateGame(id, game) {
        return axios.put(`/api/admin/games/${id}`, game, { withCredentials: true })
    },

    togglePublish(id, published) {
        return axios.patch(`/api/admin/games/${id}/publish`, { published }, { withCredentials: true })
    },

    toggleVip(id, vipOnly) {
        return axios.patch(`/api/admin/games/${id}/vip`, { vipOnly }, { withCredentials: true })
    },

    deleteGame(id) {
        return axios.delete(`/api/admin/games/${id}`, { withCredentials: true })
    },

    uploadCover(file) {
        const formData = new FormData()
        formData.append('file', file)
        return axios.post('/api/admin/games/upload-cover', formData, {
            withCredentials: true,
            headers: { 'Content-Type': 'multipart/form-data' }
        })
    }
}
