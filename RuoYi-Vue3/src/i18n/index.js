import Cookies from 'js-cookie'
import { createI18n } from 'vue-i18n'
import zhCn from './locales/zh-CN'
import jaJp from './locales/ja-JP'

export const LOCALE_KEY = 'language'
export const DEFAULT_LOCALE = 'zh-CN'
export const SUPPORT_LOCALES = ['zh-CN', 'ja-JP']

function normalizeLocale(locale) {
  return SUPPORT_LOCALES.includes(locale) ? locale : DEFAULT_LOCALE
}

export function getStoredLocale() {
  return normalizeLocale(Cookies.get(LOCALE_KEY) || globalThis.localStorage?.getItem(LOCALE_KEY))
}

const i18n = createI18n({
  legacy: false,
  globalInjection: true,
  locale: getStoredLocale(),
  fallbackLocale: DEFAULT_LOCALE,
  messages: {
    'zh-CN': zhCn,
    'ja-JP': jaJp
  }
})

export function setLocale(locale) {
  const nextLocale = normalizeLocale(locale)
  i18n.global.locale.value = nextLocale
  Cookies.set(LOCALE_KEY, nextLocale, { expires: 365 })
  globalThis.localStorage?.setItem(LOCALE_KEY, nextLocale)
  document.querySelector('html')?.setAttribute('lang', nextLocale)
}

setLocale(getStoredLocale())

export const t = i18n.global.t

export default i18n
