package com.rake.system.service.impl;

import java.util.List;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.rake.common.utils.SecurityUtils;
import com.rake.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.rake.system.domain.OutletHistory;
import com.rake.system.mapper.TabOutletMapper;
import com.rake.system.domain.TabOutlet;
import com.rake.system.service.ITabOutletService;
import com.rake.utils.AgentRoleUtil;

/**
 * 販売店管理Service业务层处理
 * 
 * @author rake
 * @date 2025-09-24
 */
@Service
public class TabOutletServiceImpl implements ITabOutletService 
{
    @Autowired
    private TabOutletMapper tabOutletMapper;

    /**
     * 查询販売店管理
     * 
     * @param id 販売店管理主键
     * @return 販売店管理
     */
    @Override
    public TabOutlet selectTabOutletById(String id)
    {
        TabOutlet tabOutlet = tabOutletMapper.selectTabOutletById(id);
        if (tabOutlet != null)
        {
            List<String> agentNames = tabOutletMapper.selectOutletAgentNames(id);
            if (StringUtils.isNotEmpty(agentNames))
            {
                tabOutlet.setAgentList(agentNames);
            }
        }
        return tabOutlet;
    }

    /**
     * 查询販売店管理列表
     * 
     * @param tabOutlet 販売店管理
     * @return 販売店管理
     */
    @Override
    public List<TabOutlet> selectTabOutletList(TabOutlet tabOutlet)
    {
        applyBusinessFlowScope(tabOutlet);
        return tabOutletMapper.selectTabOutletList(tabOutlet);
    }

    /**
     * 新增販売店管理
     * 
     * @param tabOutlet 販売店管理
     * @return 结果
     */
    @Transactional
    @Override
    public int insertTabOutlet(TabOutlet tabOutlet)
    {
        int rows = tabOutletMapper.insertTabOutlet(tabOutlet);
        syncOutletAgents(tabOutlet);
        insertOutletHistory(tabOutlet);
        return rows;
    }

    /**
     * 修改販売店管理
     * 
     * @param tabOutlet 販売店管理
     * @return 结果
     */
    @Transactional
    @Override
    public int updateTabOutlet(TabOutlet tabOutlet)
    {
        tabOutletMapper.deleteOutletHistoryByOutletId(tabOutlet.getId());
        insertOutletHistory(tabOutlet);
        int rows = tabOutletMapper.updateTabOutlet(tabOutlet);
        syncOutletAgents(tabOutlet);
        return rows;
    }

    /**
     * 批量删除販売店管理
     * 
     * @param ids 需要删除的販売店管理主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteTabOutletByIds(String[] ids)
    {
        tabOutletMapper.deleteOutletHistoryByOutletIds(ids);
        tabOutletMapper.deleteOutletAgentsByOutletIds(ids);
        return tabOutletMapper.deleteTabOutletByIds(ids);
    }

    /**
     * 删除販売店管理信息
     * 
     * @param id 販売店管理主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteTabOutletById(String id)
    {
        tabOutletMapper.deleteOutletHistoryByOutletId(id);
        tabOutletMapper.deleteOutletAgentsByOutletId(id);
        return tabOutletMapper.deleteTabOutletById(id);
    }

    /**
     * 新增販売店管理-历史记录信息
     * 
     * @param tabOutlet 販売店管理对象
     */
    public void insertOutletHistory(TabOutlet tabOutlet)
    {
        List<OutletHistory> outletHistoryList = tabOutlet.getOutletHistoryList();
        String id = tabOutlet.getId();
        if (StringUtils.isNotNull(outletHistoryList))
        {
            List<OutletHistory> list = new ArrayList<OutletHistory>();
            for (OutletHistory outletHistory : outletHistoryList)
            {
                outletHistory.setOutletId(id);
                list.add(outletHistory);
            }
            if (list.size() > 0)
            {
                tabOutletMapper.batchOutletHistory(list);
            }
        }
    }

    @Override
    public List<TabOutlet> selectTabOutletByBusinessFlow(List<String> agentNames) {
        if (StringUtils.isEmpty(agentNames))
        {
            return Collections.emptyList();
        }
        return tabOutletMapper.selectTabOutletByBusinessFlow(agentNames);
    }

    private void applyBusinessFlowScope(TabOutlet tabOutlet)
    {
        if (tabOutlet == null || AgentRoleUtil.canViewAllData(SecurityUtils.getAuthentication()))
        {
            return;
        }
        List<String> agentNames = AgentRoleUtil.getAgentRoleNames(SecurityUtils.getAuthentication());
        if (StringUtils.isEmpty(agentNames))
        {
            tabOutlet.getParams().put("businessFlows", Collections.singletonList("__NO_MATCH__"));
            return;
        }
        tabOutlet.getParams().put("businessFlows", agentNames);
    }

    private void syncOutletAgents(TabOutlet tabOutlet)
    {
        tabOutletMapper.deleteOutletAgentsByOutletId(tabOutlet.getId());
        List<String> agentNames = tabOutlet.getAgentList();
        if (StringUtils.isNotEmpty(agentNames))
        {
            tabOutletMapper.batchOutletAgents(tabOutlet.getId(), agentNames);
        }
    }
}
