import request from '@/utils/request'

export function listMonthlySales(query) {
  return request({
    url: '/system/monthlySales/list',
    method: 'get',
    params: query
  })
}

export function getMonthlySales(salesId) {
  return request({
    url: '/system/monthlySales/' + salesId,
    method: 'get'
  })
}

export function addMonthlySales(data) {
  return request({
    url: '/system/monthlySales',
    method: 'post',
    data: data
  })
}

export function updateMonthlySales(data) {
  return request({
    url: '/system/monthlySales',
    method: 'put',
    data: data
  })
}

export function delMonthlySales(salesId) {
  return request({
    url: '/system/monthlySales/' + salesId,
    method: 'delete'
  })
}
