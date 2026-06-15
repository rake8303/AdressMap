import request from '@/utils/request'

// 查询販売店管理-历史记录列表
export function listHistory(query) {
  return request({
    url: '/system/history/list',
    method: 'get',
    params: query
  })
}

// 查询販売店管理-历史记录详细
export function getHistory(historyId) {
  return request({
    url: '/system/history/' + historyId,
    method: 'get'
  })
}

// 新增販売店管理-历史记录
export function addHistory(data) {
  return request({
    url: '/system/history',
    method: 'post',
    data: data
  })
}

// 修改販売店管理-历史记录
export function updateHistory(data) {
  return request({
    url: '/system/history',
    method: 'put',
    data: data
  })
}

// 删除販売店管理-历史记录
export function delHistory(historyId) {
  return request({
    url: '/system/history/' + historyId,
    method: 'delete'
  })
}
