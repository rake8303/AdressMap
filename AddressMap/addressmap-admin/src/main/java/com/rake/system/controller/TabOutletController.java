package com.rake.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.rake.common.annotation.Log;
import com.rake.common.core.controller.BaseController;
import com.rake.common.core.domain.AjaxResult;
import com.rake.common.enums.BusinessType;
import com.rake.system.domain.TabOutlet;
import com.rake.system.service.ITabOutletService;
import com.rake.common.utils.poi.ExcelUtil;
import com.rake.common.core.page.TableDataInfo;

import org.springframework.security.core.Authentication;




/**
 * 販売店管理Controller
 * 
 * @author rake
 * @date 2025-09-24
 */
@RestController
@RequestMapping("/system/outlet")
@Api(tags = "贩卖店")
public class TabOutletController extends BaseController
{
    @Autowired
    private ITabOutletService tabOutletService;

    /**
     * 查询販売店管理列表
     */
//    @PreAuthorize("@ss.hasPermi('system:outlet:list')")
    @GetMapping("/list")
    public TableDataInfo list(TabOutlet tabOutlet,Authentication authentication)
    {
        startPage();
        List<TabOutlet> list = tabOutletService.selectTabOutletList(tabOutlet);
        return getDataTable(list);
    }

    /**
     * 导出販売店管理列表
     */
    @PreAuthorize("@ss.hasPermi('system:outlet:export')")
    @Log(title = "販売店管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TabOutlet tabOutlet)
    {
        List<TabOutlet> list = tabOutletService.selectTabOutletList(tabOutlet);
        ExcelUtil<TabOutlet> util = new ExcelUtil<TabOutlet>(TabOutlet.class);
        util.exportExcel(response, list, "販売店管理数据");
    }

    /**
     * 获取販売店管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:outlet:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(tabOutletService.selectTabOutletById(id));
    }

    /**
     * 新增販売店管理
     */
    @PreAuthorize("@ss.hasPermi('system:outlet:add')")
    @Log(title = "販売店管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TabOutlet tabOutlet)
    {
        return toAjax(tabOutletService.insertTabOutlet(tabOutlet));
    }

    /**
     * 修改販売店管理
     */
    @PreAuthorize("@ss.hasPermi('system:outlet:edit')")
    @Log(title = "販売店管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TabOutlet tabOutlet)
    {
        return toAjax(tabOutletService.updateTabOutlet(tabOutlet));
    }

    /**
     * 删除販売店管理
     */
    @PreAuthorize("@ss.hasPermi('system:outlet:remove')")
    @Log(title = "販売店管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(tabOutletService.deleteTabOutletByIds(ids));
    }

    @PreAuthorize("@ss.hasPermi('system:outlet:list')")

    @GetMapping("/listByBusinessFlow")

    @ApiOperation("获取网点代理联盟列表")
    public TableDataInfo listByBusinessFlow(TabOutlet tabOutlet,Authentication authentication)
    {
        startPage();
        List<TabOutlet> list = tabOutletService.selectTabOutletList(tabOutlet);
        return getDataTable(list);
    }



}
