package com.rake.system.mapper;

import java.util.List;
import com.rake.system.domain.OutletHistory;

/**
 * 販売店管理-历史记录Mapper接口
 * 
 * @author rake
 * @date 2025-09-19
 */
public interface OutletHistoryMapper 
{
    /**
     * 查询販売店管理-历史记录
     * 
     * @param historyId 販売店管理-历史记录主键
     * @return 販売店管理-历史记录
     */
    public OutletHistory selectOutletHistoryByHistoryId(String historyId);

    /**
     * 查询販売店管理-历史记录列表
     * 
     * @param outletHistory 販売店管理-历史记录
     * @return 販売店管理-历史记录集合
     */
    public List<OutletHistory> selectOutletHistoryList(OutletHistory outletHistory);

    /**
     * 新增販売店管理-历史记录
     * 
     * @param outletHistory 販売店管理-历史记录
     * @return 结果
     */
    public int insertOutletHistory(OutletHistory outletHistory);

    /**
     * 修改販売店管理-历史记录
     * 
     * @param outletHistory 販売店管理-历史记录
     * @return 结果
     */
    public int updateOutletHistory(OutletHistory outletHistory);

    /**
     * 删除販売店管理-历史记录
     * 
     * @param historyId 販売店管理-历史记录主键
     * @return 结果
     */
    public int deleteOutletHistoryByHistoryId(String historyId);

    /**
     * 批量删除販売店管理-历史记录
     * 
     * @param historyIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteOutletHistoryByHistoryIds(String[] historyIds);
}
