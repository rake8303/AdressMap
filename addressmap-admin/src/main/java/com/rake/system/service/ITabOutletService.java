package com.rake.system.service;

import java.util.List;
import com.rake.system.domain.TabOutlet;

/**
 * 販売店管理Service接口
 * 
 * @author rake
 * @date 2025-09-24
 */
public interface ITabOutletService 
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
     * 批量删除販売店管理
     * 
     * @param ids 需要删除的販売店管理主键集合
     * @return 结果
     */
    public int deleteTabOutletByIds(String[] ids);

    /**
     * 删除販売店管理信息
     * 
     * @param id 販売店管理主键
     * @return 结果
     */
    public int deleteTabOutletById(String id);


    /**
     * 根据业务流程查询商店列表
     *
     * @param businessFlow 业务流程
     * @return 商店列表
     */
    List<TabOutlet> selectTabOutletByBusinessFlow(List<String> agentNames);

}
