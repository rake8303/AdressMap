import request from '@/utils/request'

// 查询販売店管理列表
export function listOutlet(query) {
  return request({
    url: '/outlet/outlet/list',
    method: 'get',
    params: query
  })
}

// 查询販売店管理详细
export function getOutlet(id) {
  return request({
    url: '/outlet/outlet/' + id,
    method: 'get'
  })
}

// 新增販売店管理
export function addOutlet(data) {
  return request({
    url: '/outlet/outlet',
    method: 'post',
    data: data
  })
}

// 修改販売店管理
export function updateOutlet(data) {
  return request({
    url: '/outlet/outlet',
    method: 'put',
    data: data
  })
}

// 删除販売店管理
export function delOutlet(id) {
  return request({
    url: '/outlet/outlet/' + id,
    method: 'delete'
  })
}
