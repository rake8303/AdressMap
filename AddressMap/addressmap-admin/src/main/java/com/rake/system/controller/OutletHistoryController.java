package com.rake.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.rake.common.enums.BusinessType;
import com.rake.system.domain.OutletHistory;
import com.rake.system.service.IOutletHistoryService;
import com.rake.common.utils.poi.ExcelUtil;
import com.rake.common.core.page.TableDataInfo;

/**
 * 販売店管理-历史记录Controller
 * 
 * @author rake
 * @date 2025-09-19
 */


@RestController
@RequestMapping("/system/history")
public class OutletHistoryController extends BaseController
{
    @Autowired
    private IOutletHistoryService outletHistoryService;

    /**
     * 查询販売店管理-历史记录列表
     */
//    @PreAuthorize("@ss.hasPermi('system:history:list')")
    @GetMapping("/list")
    public TableDataInfo list(OutletHistory outletHistory)
    {
        startPage();
        List<OutletHistory> list = outletHistoryService.selectOutletHistoryList(outletHistory);
        return getDataTable(list);
    }

    /**
     * 导出販売店管理-历史记录列表
     */
//    @PreAuthorize("@ss.hasPermi('system:history:export')")
    @Log(title = "販売店管理-历史记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OutletHistory outletHistory)
    {
        List<OutletHistory> list = outletHistoryService.selectOutletHistoryList(outletHistory);
        ExcelUtil<OutletHistory> util = new ExcelUtil<OutletHistory>(OutletHistory.class);
        util.exportExcel(response, list, "販売店管理-历史记录数据");
    }

    /**
     * 获取販売店管理-历史记录详细信息
     */
//    @PreAuthorize("@ss.hasPermi('system:history:query')")
    @GetMapping(value = "/{historyId}")
    public AjaxResult getInfo(@PathVariable("historyId") String historyId)
    {
        return success(outletHistoryService.selectOutletHistoryByHistoryId(historyId));
    }

    /**
     * 新增販売店管理-历史记录
     */
//    @PreAuthorize("@ss.hasPermi('system:history:add')")
    @Log(title = "販売店管理-历史记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OutletHistory outletHistory)
    {
        return toAjax(outletHistoryService.insertOutletHistory(outletHistory));
    }

    /**
     * 修改販売店管理-历史记录
     */
//    @PreAuthorize("@ss.hasPermi('system:history:edit')")
    @Log(title = "販売店管理-历史记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OutletHistory outletHistory)
    {
        return toAjax(outletHistoryService.updateOutletHistory(outletHistory));
    }

    /**
     * 删除販売店管理-历史记录
     */
//    @PreAuthorize("@ss.hasPermi('system:history:remove')")
    @Log(title = "販売店管理-历史记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{historyIds}")
    public AjaxResult remove(@PathVariable String[] historyIds)
    {
        return toAjax(outletHistoryService.deleteOutletHistoryByHistoryIds(historyIds));
    }
}
