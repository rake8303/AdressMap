package com.rake.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.rake.system.mapper.AgentMapper;
import com.rake.system.domain.Agent;
import com.rake.system.service.IAgentService;

/**
 * 代理店管理Service业务层处理
 * 
 * @author rake
 * @date 2025-09-19
 */
@Service
public class AgentServiceImpl implements IAgentService 
{
    @Autowired
    private AgentMapper agentMapper;

    /**
     * 查询代理店管理
     * 
     * @param id 代理店管理主键
     * @return 代理店管理
     */
    @Override
    public Agent selectAgentById(String id)
    {
        return agentMapper.selectAgentById(id);
    }

    /**
     * 查询代理店管理列表
     * 
     * @param agent 代理店管理
     * @return 代理店管理
     */
    @Override
    public List<Agent> selectAgentList(Agent agent)
    {
        return agentMapper.selectAgentList(agent);
    }

    /**
     * 新增代理店管理
     * 
     * @param agent 代理店管理
     * @return 结果
     */
    @Override
    public int insertAgent(Agent agent)
    {
        return agentMapper.insertAgent(agent);
    }

    /**
     * 修改代理店管理
     * 
     * @param agent 代理店管理
     * @return 结果
     */
    @Override
    public int updateAgent(Agent agent)
    {
        return agentMapper.updateAgent(agent);
    }

    /**
     * 批量删除代理店管理
     * 
     * @param ids 需要删除的代理店管理主键
     * @return 结果
     */
    @Override
    public int deleteAgentByIds(String[] ids)
    {
        return agentMapper.deleteAgentByIds(ids);
    }

    /**
     * 删除代理店管理信息
     * 
     * @param id 代理店管理主键
     * @return 结果
     */
    @Override
    public int deleteAgentById(String id)
    {
        return agentMapper.deleteAgentById(id);
    }
}
