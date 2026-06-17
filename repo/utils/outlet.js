const FLOW_ODA = '\u7e54\u7530\u5bb6'
const FLOW_TOYOTOMI = '\u8c4a\u81e3\u5bb6'
const FLOW_TOKUGAWA = '\u5fb3\u5ddd\u5bb6'
const FLOW_TAKEDA = '\u6b66\u7530\u5bb6'
const FLOW_UESUGI = '\u4e0a\u6749\u5bb6'

export const OUTLET_AGENT_ORDER = [
  FLOW_ODA,
  FLOW_TOYOTOMI,
  FLOW_TOKUGAWA,
  FLOW_TAKEDA,
  FLOW_UESUGI
]

export const ALL_DATA_ROLE_KEYS = ['admin', 'common', 'readonly']

const BUSINESS_FLOW_ALIASES = {
  XSOL: FLOW_ODA,
  DMM: FLOW_TOYOTOMI,
  WWB: FLOW_TOKUGAWA,
  高島: FLOW_TAKEDA,
  韓華: FLOW_UESUGI
}

export function normalizeBusinessFlow(value = '') {
  const normalized = String(value || '').trim()
  return BUSINESS_FLOW_ALIASES[normalized] || normalized
}

export function sortBusinessFlows(agentList = []) {
  const uniqueValues = [...new Set((Array.isArray(agentList) ? agentList : []).map(normalizeBusinessFlow).filter(Boolean))]
  return uniqueValues.sort((left, right) => {
    const leftIndex = OUTLET_AGENT_ORDER.indexOf(left)
    const rightIndex = OUTLET_AGENT_ORDER.indexOf(right)
    if (leftIndex === -1 && rightIndex === -1) return left.localeCompare(right)
    if (leftIndex === -1) return 1
    if (rightIndex === -1) return -1
    return leftIndex - rightIndex
  })
}

export function getPrimaryBusinessFlow(agentList = []) {
  return sortBusinessFlows(agentList)[0] || ''
}

export function canViewAllData(userStore = {}) {
  const roles = Array.isArray(userStore.roles) ? userStore.roles : []
  return roles.some(role => ALL_DATA_ROLE_KEYS.includes(role))
}

export function getCurrentBusinessFlowRole(userStore = {}) {
  if (canViewAllData(userStore)) {
    return ''
  }
  const roles = Array.isArray(userStore.roles) ? userStore.roles : []
  return normalizeBusinessFlow(roles.find(role => !ALL_DATA_ROLE_KEYS.includes(role)) || '')
}

export function formatDateTime(value) {
  if (!value) return 'N/A'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) {
    return String(value)
  }
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

export function formatShortDateTime(value) {
  if (!value) return 'N/A'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) {
    return String(value)
  }
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${month}/${day} ${hours}:${minutes}`
}

export function formatNumber(value) {
  return (Number(value) || 0).toLocaleString('en-US')
}

export function normalizeMonthLabel(value) {
  const text = String(value || '').trim()
  if (!text) return ''
  if (/^\d{4}[-/]\d{1,2}$/.test(text)) {
    const [year, month] = text.split(/[-/]/)
    return `${year}-${month.padStart(2, '0')}`
  }
  return text
}

export function getMonthSortKey(value) {
  const match = normalizeMonthLabel(value).match(/^(\d{4})-(\d{2})$/)
  if (!match) return -1
  return Number(`${match[1]}${match[2]}`)
}

export function getLatestMonthlySalesRecord(rows = []) {
  const monthTotals = new Map()
  ;(Array.isArray(rows) ? rows : []).forEach(row => {
    const month = normalizeMonthLabel(row.salesMonth)
    const quantity = Number(row.quantity)
    if (!month || !Number.isFinite(quantity)) return
    monthTotals.set(month, (monthTotals.get(month) || 0) + quantity)
  })

  const latestEntry = Array.from(monthTotals.entries())
    .sort((left, right) => getMonthSortKey(right[0]) - getMonthSortKey(left[0]))[0]

  if (!latestEntry) return null
  return {
    salesMonth: latestEntry[0],
    quantity: Math.round(latestEntry[1])
  }
}

export function getLatestHistoryRecord(rows = []) {
  return [...(Array.isArray(rows) ? rows : [])]
    .sort((left, right) => new Date(right.updatedAt || right.createdAt || 0) - new Date(left.updatedAt || left.createdAt || 0))[0] || null
}

export function formatHistoryRemark(remark) {
  const text = String(remark || '').trim()
  if (!text) return 'No visit note was recorded.'

  const topicMap = {
    activity_confirm: 'activity check',
    sample_replenish: 'sample and refill check',
    merch_check: 'display check',
    monthly_review: 'monthly sales review',
    next_plan: 'next action planning',
    stock_check: 'stock check',
    quote_followup: 'quote follow-up'
  }

  const seededMatch = text.match(/topic=([^|]+)\s*\|\s*visit=(\d+)\/(\d+)/)
  if (seededMatch) {
    const [, topic, currentVisit, totalVisit] = seededMatch
    return `Main topic: ${topicMap[topic.trim()] || topic.trim()}. Visit ${currentVisit} of ${totalVisit}.`
  }

  return text
}

export function buildHistoryPayload({ outletId, remark, userStore }) {
  const flowRole = getCurrentBusinessFlowRole(userStore)
  const roles = Array.isArray(userStore.roles) ? userStore.roles : []
  const date = new Date()
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')

  return {
    outletId,
    remark: String(remark || '').trim(),
    createdBy: userStore.name || '',
    updatedBy: userStore.name || '',
    agent: canViewAllData(userStore) ? (roles[0] || '') : flowRole,
    updatedAt: `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
  }
}
