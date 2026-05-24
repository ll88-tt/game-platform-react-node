import axios from 'axios'

export const gameApi = {
    getAllGames() {
        return axios.get('/api/games', { withCredentials: true })
    },

    getGamesByCategory(category) {
        return axios.get(`/api/games/category/${encodeURIComponent(category)}`, {
            withCredentials: true
        })
    },

    searchGames(name) {
        return axios.get(`/api/games/search?name=${encodeURIComponent(name)}`, {
            withCredentials: true
        })
    }
}
