const STORAGE_KEY = 'browseHistory'
const LOCAL_MAX_ITEMS = 20

function normalizeItem(item) {
  return {
    id: item.id ?? item.gameId,
    gameId: item.gameId ?? item.id,
    name: item.name,
    link: item.link || '#',
    vipOnly: !!item.vipOnly,
    canAccess: item.canAccess !== false,
    time: item.time || item.viewedAt || new Date().toISOString()
  }
}

export function loadLocalBrowseHistory() {
  try {
    const history = JSON.parse(localStorage.getItem(STORAGE_KEY) || '[]')
    return history.map(normalizeItem)
  } catch {
    return []
  }
}

function saveLocalBrowseHistory(items) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(items.slice(0, LOCAL_MAX_ITEMS)))
}

export function recordLocalBrowse(game) {
  const history = loadLocalBrowseHistory().filter(item => item.id !== game.id)
  history.unshift(normalizeItem({
    id: game.id,
    gameId: game.id,
    name: game.name,
    link: game.link,
    vipOnly: game.vipOnly,
    time: new Date().toISOString()
  }))
  saveLocalBrowseHistory(history)
  return history
}

export function removeLocalBrowse(gameId) {
  const history = loadLocalBrowseHistory().filter(item => item.id !== gameId)
  saveLocalBrowseHistory(history)
  return history
}

export function clearLocalBrowseHistory() {
  localStorage.removeItem(STORAGE_KEY)
  return []
}

export function getLocalBrowseHistoryForSync() {
  return loadLocalBrowseHistory().map(item => ({
    gameId: item.gameId,
    viewedAt: item.time
  }))
}

export function clearLocalBrowseHistoryStorage() {
  localStorage.removeItem(STORAGE_KEY)
}
