package com.rake.system.service.impl;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.rake.common.exception.ServiceException;
import com.rake.common.utils.MessageUtils;
import com.rake.common.utils.SecurityUtils;
import com.rake.common.utils.StringUtils;
import com.rake.system.domain.OutletMonthlySales;
import com.rake.system.mapper.OutletMonthlySalesMapper;
import com.rake.system.mapper.TabOutletMapper;
import com.rake.system.service.IOutletMonthlySalesService;
import com.rake.utils.AgentRoleUtil;

@Service
public class OutletMonthlySalesServiceImpl implements IOutletMonthlySalesService
{
    @Autowired
    private OutletMonthlySalesMapper outletMonthlySalesMapper;

    @Autowired
    private TabOutletMapper tabOutletMapper;

    @Override
    public OutletMonthlySales selectOutletMonthlySalesBySalesId(Long salesId)
    {
        OutletMonthlySales outletMonthlySales = outletMonthlySalesMapper.selectOutletMonthlySalesBySalesId(salesId);
        if (outletMonthlySales != null)
        {
            validateOutletAccess(outletMonthlySales.getOutletId());
        }
        return outletMonthlySales;
    }

    @Override
    public List<OutletMonthlySales> selectOutletMonthlySalesList(OutletMonthlySales outletMonthlySales)
    {
        if (outletMonthlySales == null)
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
            outletMonthlySales.getParams().put("businessFlows", businessFlows);
        }
        return outletMonthlySalesMapper.selectOutletMonthlySalesList(outletMonthlySales);
    }

    @Override
    public int insertOutletMonthlySales(OutletMonthlySales outletMonthlySales)
    {
        validateOutletExists(outletMonthlySales);
        return outletMonthlySalesMapper.insertOutletMonthlySales(outletMonthlySales);
    }

    @Override
    public int updateOutletMonthlySales(OutletMonthlySales outletMonthlySales)
    {
        validateOutletExists(outletMonthlySales);
        return outletMonthlySalesMapper.updateOutletMonthlySales(outletMonthlySales);
    }

    @Override
    public int deleteOutletMonthlySalesBySalesIds(Long[] salesIds)
    {
        return outletMonthlySalesMapper.deleteOutletMonthlySalesBySalesIds(salesIds);
    }

    @Override
    public int deleteOutletMonthlySalesBySalesId(Long salesId)
    {
        return outletMonthlySalesMapper.deleteOutletMonthlySalesBySalesId(salesId);
    }

    private void validateOutletExists(OutletMonthlySales outletMonthlySales)
    {
        if (outletMonthlySales == null
                || outletMonthlySales.getOutletId() == null
                || outletMonthlySales.getOutletId().trim().isEmpty())
        {
            throw new ServiceException("销售店ID不能为空");
        }
        if (tabOutletMapper.selectTabOutletById(outletMonthlySales.getOutletId().trim()) == null)
        {
            throw new ServiceException("销售店不存在，无法新增月次销售");
        }
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
