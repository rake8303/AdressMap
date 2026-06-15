package com.rake.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.rake.system.domain.OutletMonthlySales;
import com.rake.system.mapper.OutletMonthlySalesMapper;
import com.rake.system.service.IOutletMonthlySalesService;

@Service
public class OutletMonthlySalesServiceImpl implements IOutletMonthlySalesService
{
    @Autowired
    private OutletMonthlySalesMapper outletMonthlySalesMapper;

    @Override
    public OutletMonthlySales selectOutletMonthlySalesBySalesId(Long salesId)
    {
        return outletMonthlySalesMapper.selectOutletMonthlySalesBySalesId(salesId);
    }

    @Override
    public List<OutletMonthlySales> selectOutletMonthlySalesList(OutletMonthlySales outletMonthlySales)
    {
        return outletMonthlySalesMapper.selectOutletMonthlySalesList(outletMonthlySales);
    }

    @Override
    public int insertOutletMonthlySales(OutletMonthlySales outletMonthlySales)
    {
        return outletMonthlySalesMapper.insertOutletMonthlySales(outletMonthlySales);
    }

    @Override
    public int updateOutletMonthlySales(OutletMonthlySales outletMonthlySales)
    {
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
}
