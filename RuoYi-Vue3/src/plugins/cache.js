const sessionCache = {
  set (key, value) {
    const storage = globalThis.sessionStorage
    if (!storage) {
      return
    }
    if (key != null && value != null) {
      storage.setItem(key, value)
    }
  },
  get (key) {
    const storage = globalThis.sessionStorage
    if (!storage) {
      return null
    }
    if (key == null) {
      return null
    }
    return storage.getItem(key)
  },
  setJSON (key, jsonValue) {
    if (jsonValue != null) {
      this.set(key, JSON.stringify(jsonValue))
    }
  },
  getJSON (key) {
    const value = this.get(key)
    if (value != null) {
      return JSON.parse(value)
    }
    return null
  },
  remove (key) {
    globalThis.sessionStorage?.removeItem(key)
  }
}
const localCache = {
  set (key, value) {
    const storage = globalThis.localStorage
    if (!storage) {
      return
    }
    if (key != null && value != null) {
      storage.setItem(key, value)
    }
  },
  get (key) {
    const storage = globalThis.localStorage
    if (!storage) {
      return null
    }
    if (key == null) {
      return null
    }
    return storage.getItem(key)
  },
  setJSON (key, jsonValue) {
    if (jsonValue != null) {
      this.set(key, JSON.stringify(jsonValue))
    }
  },
  getJSON (key) {
    const value = this.get(key)
    if (value != null) {
      return JSON.parse(value)
    }
    return null
  },
  remove (key) {
    globalThis.localStorage?.removeItem(key)
  }
}

export default {
  /**
   * 会话级缓存
   */
  session: sessionCache,
  /**
   * 本地缓存
   */
  local: localCache
}
