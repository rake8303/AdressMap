package com.rake.system.service;

import java.util.List;
import com.rake.system.domain.Agent;

/**
 * 代理店管理Service接口
 * 
 * @author rake
 * @date 2025-09-19
 */
public interface IAgentService 
{
    /**
     * 查询代理店管理
     * 
     * @param id 代理店管理主键
     * @return 代理店管理
     */
    public Agent selectAgentById(String id);

    /**
     * 查询代理店管理列表
     * 
     * @param agent 代理店管理
     * @return 代理店管理集合
     */
    public List<Agent> selectAgentList(Agent agent);

    /**
     * 新增代理店管理
     * 
     * @param agent 代理店管理
     * @return 结果
     */
    public int insertAgent(Agent agent);

    /**
     * 修改代理店管理
     * 
     * @param agent 代理店管理
     * @return 结果
     */
    public int updateAgent(Agent agent);

    /**
     * 批量删除代理店管理
     * 
     * @param ids 需要删除的代理店管理主键集合
     * @return 结果
     */
    public int deleteAgentByIds(String[] ids);

    /**
     * 删除代理店管理信息
     * 
     * @param id 代理店管理主键
     * @return 结果
     */
    public int deleteAgentById(String id);
}
