package com.rake.system.service.impl;

import com.rake.common.exception.ServiceException;
import com.rake.common.utils.MessageUtils;
import com.rake.common.utils.SecurityUtils;
import com.rake.common.utils.StringUtils;
import com.rake.system.domain.OutletMonthlySales;
import com.rake.system.mapper.OutletMonthlySalesMapper;
import com.rake.system.mapper.TabOutletMapper;
import com.rake.system.service.IOutletMonthlySalesService;
import com.rake.utils.AgentRoleUtil;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        List<String> businessFlows = AgentRoleUtil.getBusinessFlowNames(SecurityUtils.getAuthentication());
        if (!AgentRoleUtil.canViewAllData(SecurityUtils.getAuthentication()))
        {
            if (businessFlows.isEmpty())
            {
                return Collections.emptyList();
            }
            validateRequestedOutletAccess(outletMonthlySales.getOutletId());
            outletMonthlySales.getParams().put("businessFlows", businessFlows);
        }
        return outletMonthlySalesMapper.selectOutletMonthlySalesList(outletMonthlySales);
    }

    @Override
    public int insertOutletMonthlySales(OutletMonthlySales outletMonthlySales)
    {
        validateWritableMonthlySales(outletMonthlySales);
        return outletMonthlySalesMapper.insertOutletMonthlySales(outletMonthlySales);
    }

    @Override
    public int updateOutletMonthlySales(OutletMonthlySales outletMonthlySales)
    {
        validateWritableMonthlySales(outletMonthlySales);
        return outletMonthlySalesMapper.updateOutletMonthlySales(outletMonthlySales);
    }

    @Override
    public int deleteOutletMonthlySalesBySalesIds(Long[] salesIds)
    {
        if (salesIds != null)
        {
            for (Long salesId : salesIds)
            {
                validateSalesAccess(salesId);
            }
        }
        return outletMonthlySalesMapper.deleteOutletMonthlySalesBySalesIds(salesIds);
    }

    @Override
    public int deleteOutletMonthlySalesBySalesId(Long salesId)
    {
        validateSalesAccess(salesId);
        return outletMonthlySalesMapper.deleteOutletMonthlySalesBySalesId(salesId);
    }

    private void validateWritableMonthlySales(OutletMonthlySales outletMonthlySales)
    {
        if (outletMonthlySales != null && outletMonthlySales.getSalesId() != null)
        {
            validateSalesAccess(outletMonthlySales.getSalesId());
        }
        validateOutletExists(outletMonthlySales);
        validateOutletAccess(outletMonthlySales.getOutletId());
    }

    private void validateOutletExists(OutletMonthlySales outletMonthlySales)
    {
        if (outletMonthlySales == null || StringUtils.isBlank(outletMonthlySales.getOutletId()))
        {
            throw new ServiceException("Outlet ID cannot be empty");
        }
        if (tabOutletMapper.selectTabOutletById(outletMonthlySales.getOutletId().trim()) == null)
        {
            throw new ServiceException("Outlet does not exist");
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
        List<String> businessFlows = AgentRoleUtil.getBusinessFlowNames(SecurityUtils.getAuthentication());
        List<String> outletAgents = AgentRoleUtil.keepPrimaryBusinessFlow(tabOutletMapper.selectOutletAgentNames(outletId));
        if (businessFlows.isEmpty() || StringUtils.isEmpty(outletAgents) || Collections.disjoint(businessFlows, outletAgents))
        {
            throw new ServiceException(MessageUtils.message("no.view.permission", String.join(",", businessFlows)));
        }
    }

    private void validateSalesAccess(Long salesId)
    {
        OutletMonthlySales outletMonthlySales = outletMonthlySalesMapper.selectOutletMonthlySalesBySalesId(salesId);
        if (outletMonthlySales != null)
        {
            validateOutletAccess(outletMonthlySales.getOutletId());
        }
    }
}
