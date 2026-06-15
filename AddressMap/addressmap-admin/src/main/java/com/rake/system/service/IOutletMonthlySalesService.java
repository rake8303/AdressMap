package com.rake.system.service;

import java.util.List;
import com.rake.system.domain.OutletMonthlySales;

public interface IOutletMonthlySalesService
{
    OutletMonthlySales selectOutletMonthlySalesBySalesId(Long salesId);

    List<OutletMonthlySales> selectOutletMonthlySalesList(OutletMonthlySales outletMonthlySales);

    int insertOutletMonthlySales(OutletMonthlySales outletMonthlySales);

    int updateOutletMonthlySales(OutletMonthlySales outletMonthlySales);

    int deleteOutletMonthlySalesBySalesIds(Long[] salesIds);

    int deleteOutletMonthlySalesBySalesId(Long salesId);
}
