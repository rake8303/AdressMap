package com.rake.system.service.impl;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.rake.common.exception.ServiceException;
import com.rake.common.utils.MessageUtils;
import com.rake.common.utils.SecurityUtils;
import com.rake.common.utils.StringUtils;
import com.rake.system.mapper.OutletHistoryMapper;
import com.rake.system.mapper.TabOutletMapper;
import com.rake.system.domain.OutletHistory;
import com.rake.system.service.IOutletHistoryService;
import com.rake.utils.AgentRoleUtil;

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

    @Autowired
    private TabOutletMapper tabOutletMapper;

    /**
     * 查询販売店管理-历史记录
     * 
     * @param historyId 販売店管理-历史记录主键
     * @return 販売店管理-历史记录
     */
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

    /**
     * 查询販売店管理-历史记录列表
     * 
     * @param outletHistory 販売店管理-历史记录
     * @return 販売店管理-历史记录
     */
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
            outletHistory.getParams().put("businessFlows", businessFlows);
        }
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

    private void validateOutletAccess(String outletId)
    {
        if (StringUtils.isBlank(outletId) || AgentRoleUtil.canViewAllData(SecurityUtils.getAuthentication()))
        {
            return;
        }
        List<String> businessFlows = getCurrentBusinessFlows();
        List<String> outletAgents = tabOutletMapper.selectOutletAgentNames(outletId);
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
