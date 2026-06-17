package com.rake.system.mapper;

import com.rake.system.domain.OutletMonthlySales;
import java.util.List;

public interface OutletMonthlySalesMapper
{
    OutletMonthlySales selectOutletMonthlySalesBySalesId(Long salesId);

    List<OutletMonthlySales> selectOutletMonthlySalesList(OutletMonthlySales outletMonthlySales);

    int insertOutletMonthlySales(OutletMonthlySales outletMonthlySales);

    int updateOutletMonthlySales(OutletMonthlySales outletMonthlySales);

    int deleteOutletMonthlySalesBySalesId(Long salesId);

    int deleteOutletMonthlySalesBySalesIds(Long[] salesIds);
}
