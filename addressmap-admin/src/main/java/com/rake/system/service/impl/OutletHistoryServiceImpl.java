package com.rake.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.rake.system.mapper.OutletHistoryMapper;
import com.rake.system.domain.OutletHistory;
import com.rake.system.service.IOutletHistoryService;

/**
 * 販売店管理-历史记录Service业务层处理
 * 
 * @author rake
 * @date 2025-09-19
 */
@Service
public class OutletHistoryServiceImpl implements IOutletHistoryService 
{
    @Autowired
    private OutletHistoryMapper outletHistoryMapper;

    /**
     * 查询販売店管理-历史记录
     * 
     * @param historyId 販売店管理-历史记录主键
     * @return 販売店管理-历史记录
     */
    @Override
    public OutletHistory selectOutletHistoryByHistoryId(String historyId)
    {
        return outletHistoryMapper.selectOutletHistoryByHistoryId(historyId);
    }

    /**
     * 查询販売店管理-历史记录列表
     * 
     * @param outletHistory 販売店管理-历史记录
     * @return 販売店管理-历史记录
     */
    @Override
    public List<OutletHistory> selectOutletHistoryList(OutletHistory outletHistory)
    {
        return outletHistoryMapper.selectOutletHistoryList(outletHistory);
    }

    /**
     * 新增販売店管理-历史记录
     * 
     * @param outletHistory 販売店管理-历史记录
     * @return 结果
     */
    @Override
    public int insertOutletHistory(OutletHistory outletHistory)
    {
        return outletHistoryMapper.insertOutletHistory(outletHistory);
    }

    /**
     * 修改販売店管理-历史记录
     * 
     * @param outletHistory 販売店管理-历史记录
     * @return 结果
     */
    @Override
    public int updateOutletHistory(OutletHistory outletHistory)
    {
        return outletHistoryMapper.updateOutletHistory(outletHistory);
    }

    /**
     * 批量删除販売店管理-历史记录
     * 
     * @param historyIds 需要删除的販売店管理-历史记录主键
     * @return 结果
     */
    @Override
    public int deleteOutletHistoryByHistoryIds(String[] historyIds)
    {
        return outletHistoryMapper.deleteOutletHistoryByHistoryIds(historyIds);
    }

    /**
     * 删除販売店管理-历史记录信息
     * 
     * @param historyId 販売店管理-历史记录主键
     * @return 结果
     */
    @Override
    public int deleteOutletHistoryByHistoryId(String historyId)
    {
        return outletHistoryMapper.deleteOutletHistoryByHistoryId(historyId);
    }
}
