import request from '@/utils/request'

// 查询販売店管理列表
export function listHanbaiten(query) {
  return request({
    url: '/system/hanbaiten/list',
    method: 'get',
    params: query
  })
}

// 查询販売店管理详细
export function getHanbaiten(id) {
  return request({
    url: '/system/hanbaiten/' + id,
    method: 'get'
  })
}

// 新增販売店管理
export function addHanbaiten(data) {
  return request({
    url: '/system/hanbaiten',
    method: 'post',
    data: data
  })
}

// 修改販売店管理
export function updateHanbaiten(data) {
  return request({
    url: '/system/hanbaiten',
    method: 'put',
    data: data
  })
}

// 删除販売店管理
export function delHanbaiten(id) {
  return request({
    url: '/system/hanbaiten/' + id,
    method: 'delete'
  })
}
