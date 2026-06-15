import request from '@/utils/request'

// 查询販売店管理列表
export function listOutlet(query) {
  return request({
    url: '/system/outlet/list',
    method: 'get',
    params: query
  })
}


export function listByBusinessFlow(query) {
  return request({
    url: '/system/outlet/listByBusinessFlow',
    method: 'get',
    params: query
  })
}

// 查询販売店管理详细
export function getOutlet(id) {
  return request({
    url: '/system/outlet/' + id,
    method: 'get'
  })
}

// 新增販売店管理
export function addOutlet(data) {
  return request({
    url: '/system/outlet',
    method: 'post',
    data: data
  })
}

// 修改販売店管理
export function updateOutlet(data) {
  return request({
    url: '/system/outlet',
    method: 'put',
    data: data
  })
}

// 删除販売店管理
export function delOutlet(id) {
  return request({
    url: '/system/outlet/' + id,
    method: 'delete'
  })
}
