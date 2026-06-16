package com.rake.system.service.impl;

import java.util.List;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.rake.common.exception.ServiceException;
import com.rake.common.utils.MessageUtils;
import com.rake.common.utils.StringUtils;
import com.rake.common.utils.SecurityUtils;
import org.springframework.transaction.annotation.Transactional;
import com.rake.system.domain.OutletHistory;
import com.rake.system.domain.OutletMonthlySales;
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
            validateOutletAccess(agentNames);
            if (AgentRoleUtil.canViewAllData(SecurityUtils.getAuthentication()))
            {
                tabOutlet.setAgentList(agentNames);
            }
            else
            {
                List<String> visibleAgentNames = new ArrayList<String>(agentNames);
                visibleAgentNames.retainAll(getCurrentBusinessFlows());
                tabOutlet.setAgentList(visibleAgentNames);
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
        if (tabOutlet == null)
        {
            return Collections.emptyList();
        }
        List<String> currentBusinessFlows = getCurrentBusinessFlows();
        applyBusinessFlowScope(tabOutlet);
        if (!AgentRoleUtil.canViewAllData(SecurityUtils.getAuthentication())
                && currentBusinessFlows.isEmpty())
        {
            return Collections.emptyList();
        }
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
        applyEditableBusinessFlows(tabOutlet);
        int rows = tabOutletMapper.insertTabOutlet(tabOutlet);
        syncOutletAgents(tabOutlet);
        insertOutletHistory(tabOutlet);
        insertOutletMonthlySales(tabOutlet);
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
        applyEditableBusinessFlows(tabOutlet);
        tabOutletMapper.deleteOutletHistoryByOutletId(tabOutlet.getId());
        insertOutletHistory(tabOutlet);
        if (StringUtils.isNotNull(tabOutlet.getMonthlySalesList()))
        {
            tabOutletMapper.deleteOutletMonthlySalesByOutletId(tabOutlet.getId());
            insertOutletMonthlySales(tabOutlet);
        }
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
        tabOutletMapper.deleteOutletMonthlySalesByOutletIds(ids);
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
        tabOutletMapper.deleteOutletMonthlySalesByOutletId(id);
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

    private void syncOutletAgents(TabOutlet tabOutlet)
    {
        tabOutletMapper.deleteOutletAgentsByOutletId(tabOutlet.getId());
        List<String> agentNames = tabOutlet.getAgentList();
        if (StringUtils.isNotEmpty(agentNames))
        {
            tabOutletMapper.batchOutletAgents(tabOutlet.getId(), Collections.singletonList(agentNames.get(0)));
        }
    }

    public void insertOutletMonthlySales(TabOutlet tabOutlet)
    {
        List<OutletMonthlySales> monthlySalesList = tabOutlet.getMonthlySalesList();
        String id = tabOutlet.getId();
        if (StringUtils.isNotNull(monthlySalesList))
        {
            List<OutletMonthlySales> list = new ArrayList<OutletMonthlySales>();
            for (OutletMonthlySales monthlySales : monthlySalesList)
            {
                monthlySales.setOutletId(id);
                list.add(monthlySales);
            }
            if (list.size() > 0)
            {
                tabOutletMapper.batchOutletMonthlySales(list);
            }
        }
    }

    private void applyBusinessFlowScope(TabOutlet tabOutlet)
    {
        List<String> businessFlows = getCurrentBusinessFlows();
        if (tabOutlet == null
                || AgentRoleUtil.canViewAllData(SecurityUtils.getAuthentication())
                || businessFlows.isEmpty())
        {
            return;
        }
        tabOutlet.getParams().put("businessFlows", businessFlows);
    }

    private List<String> getCurrentBusinessFlows()
    {
        return AgentRoleUtil.getBusinessFlowNames(SecurityUtils.getAuthentication());
    }

    private void validateOutletAccess(List<String> outletAgents)
    {
        if (AgentRoleUtil.canViewAllData(SecurityUtils.getAuthentication()))
        {
            return;
        }
        List<String> businessFlows = getCurrentBusinessFlows();
        if (businessFlows.isEmpty() || StringUtils.isEmpty(outletAgents) || Collections.disjoint(businessFlows, outletAgents))
        {
            throw new ServiceException(MessageUtils.message("no.view.permission", String.join(",", businessFlows)));
        }
    }

    private void applyEditableBusinessFlows(TabOutlet tabOutlet)
    {
        if (tabOutlet == null || AgentRoleUtil.canViewAllData(SecurityUtils.getAuthentication()))
        {
            return;
        }
        List<String> businessFlows = getCurrentBusinessFlows();
        if (businessFlows.isEmpty())
        {
            throw new ServiceException(MessageUtils.message("no.view.permission", ""));
        }
        tabOutlet.setAgentList(Collections.singletonList(businessFlows.get(0)));
    }
}
