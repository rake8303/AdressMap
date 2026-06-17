import request from '@/utils/request'

export function listHistory(params) {
  return request({
    url: '/system/history/list',
    method: 'get',
    params
  })
}

export function addHistory(data) {
  return request({
    url: '/system/history',
    method: 'post',
    data
  })
}
