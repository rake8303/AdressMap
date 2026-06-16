package com.rake.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.rake.common.annotation.Log;
import com.rake.common.core.controller.BaseController;
import com.rake.common.core.domain.AjaxResult;
import com.rake.common.core.page.TableDataInfo;
import com.rake.common.enums.BusinessType;
import com.rake.common.utils.poi.ExcelUtil;
import com.rake.system.domain.OutletMonthlySales;
import com.rake.system.service.IOutletMonthlySalesService;

@RestController
@RequestMapping("/system/monthlySales")
public class OutletMonthlySalesController extends BaseController
{
    // 权限修改前：本 Controller 的列表/导出/详情/新增/编辑/删除接口均未声明 @PreAuthorize。
    @Autowired
    private IOutletMonthlySalesService outletMonthlySalesService;

    @GetMapping("/list")
    public TableDataInfo list(OutletMonthlySales outletMonthlySales)
    {
        startPage();
        List<OutletMonthlySales> list = outletMonthlySalesService.selectOutletMonthlySalesList(outletMonthlySales);
        return getDataTable(list);
    }

    @PostMapping("/export")
    public void export(HttpServletResponse response, OutletMonthlySales outletMonthlySales)
    {
        List<OutletMonthlySales> list = outletMonthlySalesService.selectOutletMonthlySalesList(outletMonthlySales);
        ExcelUtil<OutletMonthlySales> util = new ExcelUtil<OutletMonthlySales>(OutletMonthlySales.class);
        util.exportExcel(response, list, "月次販売台数");
    }

    @GetMapping(value = "/{salesId}")
    public AjaxResult getInfo(@PathVariable("salesId") Long salesId)
    {
        return success(outletMonthlySalesService.selectOutletMonthlySalesBySalesId(salesId));
    }

    @Log(title = "月次販売台数", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OutletMonthlySales outletMonthlySales)
    {
        return toAjax(outletMonthlySalesService.insertOutletMonthlySales(outletMonthlySales));
    }

    @Log(title = "月次販売台数", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OutletMonthlySales outletMonthlySales)
    {
        return toAjax(outletMonthlySalesService.updateOutletMonthlySales(outletMonthlySales));
    }

    @Log(title = "月次販売台数", businessType = BusinessType.DELETE)
    @DeleteMapping("/{salesIds}")
    public AjaxResult remove(@PathVariable Long[] salesIds)
    {
        return toAjax(outletMonthlySalesService.deleteOutletMonthlySalesBySalesIds(salesIds));
    }
}
