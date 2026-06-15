package com.rake.system.mapper;

import java.util.List;
import com.rake.system.domain.OutletMonthlySales;

public interface OutletMonthlySalesMapper
{
    OutletMonthlySales selectOutletMonthlySalesBySalesId(Long salesId);

    List<OutletMonthlySales> selectOutletMonthlySalesList(OutletMonthlySales outletMonthlySales);

    int insertOutletMonthlySales(OutletMonthlySales outletMonthlySales);

    int updateOutletMonthlySales(OutletMonthlySales outletMonthlySales);

    int deleteOutletMonthlySalesBySalesId(Long salesId);

    int deleteOutletMonthlySalesBySalesIds(Long[] salesIds);
}
