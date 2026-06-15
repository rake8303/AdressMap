import request from '@/utils/request'

// 查询代理店管理列表
export function listAgent(query) {
  return request({
    url: '/system/agent/list',
    method: 'get',
    params: query
  })
}

// 查询代理店管理详细
export function getAgent(id) {
  return request({
    url: '/system/agent/' + id,
    method: 'get'
  })
}

// 新增代理店管理
export function addAgent(data) {
  return request({
    url: '/system/agent',
    method: 'post',
    data: data
  })
}

// 修改代理店管理
export function updateAgent(data) {
  return request({
    url: '/system/agent',
    method: 'put',
    data: data
  })
}

// 删除代理店管理
export function delAgent(id) {
  return request({
    url: '/system/agent/' + id,
    method: 'delete'
  })
}
