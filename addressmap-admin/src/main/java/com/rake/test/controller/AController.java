package com.rake.test.controller;

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
import com.rake.test.domain.A;
import com.rake.test.service.IAService;
import com.rake.common.utils.poi.ExcelUtil;
import com.rake.common.core.page.TableDataInfo;

/**
 * testController
 * 
 * @author rake
 * @date 2025-08-17
 */
@RestController
@RequestMapping("/test/a")
public class AController extends BaseController
{
    @Autowired
    private IAService aService;

    /**
     * 查询test列表
     */
    @PreAuthorize("@ss.hasPermi('test:a:list')")
    @GetMapping("/list")
    public TableDataInfo list(A a)
    {
        startPage();
        List<A> list = aService.selectAList(a);
        return getDataTable(list);
    }

    /**
     * 导出test列表
     */
    @PreAuthorize("@ss.hasPermi('test:a:export')")
    @Log(title = "test", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, A a)
    {
        List<A> list = aService.selectAList(a);
        ExcelUtil<A> util = new ExcelUtil<A>(A.class);
        util.exportExcel(response, list, "test数据");
    }

    /**
     * 获取test详细信息
     */
    @PreAuthorize("@ss.hasPermi('test:a:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(aService.selectAById(id));
    }

    /**
     * 新增test
     */
    @PreAuthorize("@ss.hasPermi('test:a:add')")
    @Log(title = "test", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody A a)
    {
        return toAjax(aService.insertA(a));
    }

    /**
     * 修改test
     */
    @PreAuthorize("@ss.hasPermi('test:a:edit')")
    @Log(title = "test", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody A a)
    {
        return toAjax(aService.updateA(a));
    }

    /**
     * 删除test
     */
    @PreAuthorize("@ss.hasPermi('test:a:remove')")
    @Log(title = "test", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(aService.deleteAByIds(ids));
    }
}
