import request from '@/utils/request'

export function listMonthlySales(params) {
  return request({
    url: '/system/monthlySales/list',
    method: 'get',
    params
  })
}
