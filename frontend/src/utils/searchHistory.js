const STORAGE_KEY = 'searchHistory'
const MAX_ITEMS = 10

export function loadSearchHistory() {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_KEY) || '[]')
  } catch {
    return []
  }
}

export function saveSearchKeyword(keyword) {
  const trimmed = keyword?.trim()
  if (!trimmed) return

  const history = loadSearchHistory().filter(item => item.keyword !== trimmed)
  history.unshift({ keyword: trimmed, time: new Date().toISOString() })
  localStorage.setItem(STORAGE_KEY, JSON.stringify(history.slice(0, MAX_ITEMS)))
}

export function removeSearchKeyword(keyword) {
  const history = loadSearchHistory().filter(item => item.keyword !== keyword)
  localStorage.setItem(STORAGE_KEY, JSON.stringify(history))
}

export function clearSearchHistory() {
  localStorage.removeItem(STORAGE_KEY)
}
