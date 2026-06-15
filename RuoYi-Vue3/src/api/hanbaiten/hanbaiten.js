import request from '@/utils/request'

// 查询販売店列表
export function listHanbaiten(query) {
  return request({
    url: '/hanbaiten/hanbaiten/list',
    method: 'get',
    params: query
  })
}

// 查询販売店详细
export function getHanbaiten(id) {
  return request({
    url: '/hanbaiten/hanbaiten/' + id,
    method: 'get'
  })
}

// 新增販売店
export function addHanbaiten(data) {
  return request({
    url: '/hanbaiten/hanbaiten',
    method: 'post',
    data: data
  })
}

// 修改販売店
export function updateHanbaiten(data) {
  return request({
    url: '/hanbaiten/hanbaiten',
    method: 'put',
    data: data
  })
}

// 删除販売店
export function delHanbaiten(id) {
  return request({
    url: '/hanbaiten/hanbaiten/' + id,
    method: 'delete'
  })
}
