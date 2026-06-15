import request from '@/utils/request'

// 获取 /outletAgentUnion/list 列表
export function listOutletAgentUnion(query) {
    return request({
        url: '/outletAgentUnion/list',
        method: 'get',
        params: query
    })
}


export function listOutletAgentUnionbybusinessflow(query) {
    return request({
        url: '/outletAgentUnion/listbybusinessflow',
        method: 'get',
        params: query
    })
}