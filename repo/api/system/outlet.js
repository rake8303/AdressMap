import request from '@/utils/request'

export function listOutlet(params) {
  return request({
    url: '/system/outlet/list',
    method: 'get',
    params
  })
}

export function getOutlet(id) {
  return request({
    url: `/system/outlet/${id}`,
    method: 'get'
  })
}
