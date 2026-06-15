package com.rake.system.mapper;

import java.util.List;
import com.rake.system.domain.TabOutlet;
import com.rake.system.domain.OutletHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 販売店管理Mapper接口
 * 
 * @author rake
 * @date 2025-09-24
 */

public interface TabOutletMapper 
{
    /**
     * 查询販売店管理
     * 
     * @param id 販売店管理主键
     * @return 販売店管理
     */
    public TabOutlet selectTabOutletById(String id);

    /**
     * 查询販売店管理列表
     * 
     * @param tabOutlet 販売店管理
     * @return 販売店管理集合
     */
    public List<TabOutlet> selectTabOutletList(TabOutlet tabOutlet);

    /**
     * 新增販売店管理
     * 
     * @param tabOutlet 販売店管理
     * @return 结果
     */
    public int insertTabOutlet(TabOutlet tabOutlet);

    /**
     * 修改販売店管理
     * 
     * @param tabOutlet 販売店管理
     * @return 结果
     */
    public int updateTabOutlet(TabOutlet tabOutlet);

    /**
     * 删除販売店管理
     * 
     * @param id 販売店管理主键
     * @return 结果
     */
    public int deleteTabOutletById(String id);

    /**
     * 批量删除販売店管理
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTabOutletByIds(String[] ids);

    /**
     * 批量删除販売店管理-历史记录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteOutletHistoryByOutletIds(String[] ids);
    
    /**
     * 批量新增販売店管理-历史记录
     * 
     * @param outletHistoryList 販売店管理-历史记录列表
     * @return 结果
     */
    public int batchOutletHistory(List<OutletHistory> outletHistoryList);
    

    /**
     * 通过販売店管理主键删除販売店管理-历史记录信息
     * 
     * @param id 販売店管理ID
     * @return 结果
     */
    public int deleteOutletHistoryByOutletId(String id);

    /**
     * 根据业务流程查询商店列表
     *
     * @param businessFlow 业务流程
     * @return 商店列表
     */
    List<TabOutlet> selectTabOutletByBusinessFlow(@Param("agentNames") List<String> agentNames);

    List<String> selectOutletAgentNames(@Param("outletId") String outletId);

    int deleteOutletAgentsByOutletId(@Param("outletId") String outletId);

    int deleteOutletAgentsByOutletIds(String[] outletIds);

    int batchOutletAgents(@Param("outletId") String outletId, @Param("agentNames") List<String> agentNames);

}
