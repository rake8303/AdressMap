package com.rake.system.service.impl;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.rake.common.exception.ServiceException;
import com.rake.common.utils.MessageUtils;
import com.rake.common.utils.SecurityUtils;
import com.rake.common.utils.StringUtils;
import com.rake.system.domain.OutletHistory;
import com.rake.system.mapper.OutletHistoryMapper;
import com.rake.system.mapper.TabOutletMapper;
import com.rake.system.service.IOutletHistoryService;
import com.rake.utils.AgentRoleUtil;

@Service
public class OutletHistoryServiceImpl implements IOutletHistoryService
{
    @Autowired
    private OutletHistoryMapper outletHistoryMapper;

    @Autowired
    private TabOutletMapper tabOutletMapper;

    @Override
    public OutletHistory selectOutletHistoryByHistoryId(String historyId)
    {
        OutletHistory outletHistory = outletHistoryMapper.selectOutletHistoryByHistoryId(historyId);
        if (outletHistory != null)
        {
            validateOutletAccess(outletHistory.getOutletId());
        }
        return outletHistory;
    }

    @Override
    public List<OutletHistory> selectOutletHistoryList(OutletHistory outletHistory)
    {
        if (outletHistory == null)
        {
            return Collections.emptyList();
        }
        List<String> businessFlows = getCurrentBusinessFlows();
        if (!AgentRoleUtil.canViewAllData(SecurityUtils.getAuthentication()))
        {
            if (businessFlows.isEmpty())
            {
                return Collections.emptyList();
            }
            validateRequestedOutletAccess(outletHistory.getOutletId());
            outletHistory.getParams().put("businessFlows", businessFlows);
        }
        return outletHistoryMapper.selectOutletHistoryList(outletHistory);
    }

    @Override
    public int insertOutletHistory(OutletHistory outletHistory)
    {
        validateWritableHistory(outletHistory);
        return outletHistoryMapper.insertOutletHistory(outletHistory);
    }

    @Override
    public int updateOutletHistory(OutletHistory outletHistory)
    {
        validateWritableHistory(outletHistory);
        return outletHistoryMapper.updateOutletHistory(outletHistory);
    }

    @Override
    public int deleteOutletHistoryByHistoryIds(String[] historyIds)
    {
        if (historyIds != null)
        {
            for (String historyId : historyIds)
            {
                validateHistoryAccess(historyId);
            }
        }
        return outletHistoryMapper.deleteOutletHistoryByHistoryIds(historyIds);
    }

    @Override
    public int deleteOutletHistoryByHistoryId(String historyId)
    {
        validateHistoryAccess(historyId);
        return outletHistoryMapper.deleteOutletHistoryByHistoryId(historyId);
    }

    private void validateWritableHistory(OutletHistory outletHistory)
    {
        if (outletHistory == null)
        {
            throw new ServiceException("访问履历不能为空");
        }
        validateExistingHistoryAccess(outletHistory.getHistoryId());
        validateOutletAccess(outletHistory.getOutletId());
        if (!AgentRoleUtil.canViewAllData(SecurityUtils.getAuthentication()))
        {
            outletHistory.setAgent(AgentRoleUtil.getCurrentBusinessFlow(SecurityUtils.getAuthentication()));
        }
    }

    private void validateHistoryAccess(String historyId)
    {
        OutletHistory outletHistory = outletHistoryMapper.selectOutletHistoryByHistoryId(historyId);
        if (outletHistory != null)
        {
            validateOutletAccess(outletHistory.getOutletId());
        }
    }

    private void validateExistingHistoryAccess(String historyId)
    {
        if (StringUtils.isNotBlank(historyId))
        {
            validateHistoryAccess(historyId);
        }
    }

    private void validateRequestedOutletAccess(String outletId)
    {
        if (StringUtils.isNotBlank(outletId))
        {
            validateOutletAccess(outletId);
        }
    }

    private void validateOutletAccess(String outletId)
    {
        if (StringUtils.isBlank(outletId) || AgentRoleUtil.canViewAllData(SecurityUtils.getAuthentication()))
        {
            return;
        }
        List<String> businessFlows = getCurrentBusinessFlows();
        List<String> outletAgents = AgentRoleUtil.keepPrimaryBusinessFlow(tabOutletMapper.selectOutletAgentNames(outletId));
        if (businessFlows.isEmpty() || StringUtils.isEmpty(outletAgents) || Collections.disjoint(businessFlows, outletAgents))
        {
            throw new ServiceException(MessageUtils.message("no.view.permission", String.join(",", businessFlows)));
        }
    }

    private List<String> getCurrentBusinessFlows()
    {
        return AgentRoleUtil.getBusinessFlowNames(SecurityUtils.getAuthentication());
    }
}
