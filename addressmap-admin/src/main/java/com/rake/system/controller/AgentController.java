package com.rake.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.rake.utils.UserRoleUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
import com.rake.system.domain.Agent;
import com.rake.system.service.IAgentService;
import com.rake.common.utils.poi.ExcelUtil;
import com.rake.common.core.page.TableDataInfo;

/**
 * 代理店管理Controller
 * 
 * @author rake
 * @date 2025-09-19
 */
@RestController
@RequestMapping("/system/agent")
public class AgentController extends BaseController
{
    @Autowired
    private IAgentService agentService;

    /**
     * 查询代理店管理列表
     */
    @PreAuthorize("@ss.hasPermi('system:agent:list')")
    @GetMapping("/list")
    public TableDataInfo list(Agent agent , Authentication authentication)
    {
//        System.out.println("---------");
        List<String> roleNames = UserRoleUtil.getRoleListName(authentication);
//        System.out.println(roleNames.get(0));
//        System.out.println("---------");

        startPage();
        List<Agent> list = agentService.selectAgentList(agent);
        return getDataTable(list);
    }


    /**
     * 导出代理店管理列表
     */
    @PreAuthorize("@ss.hasPermi('system:agent:export')")
    @Log(title = "代理店管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Agent agent)
    {
        List<Agent> list = agentService.selectAgentList(agent);
        ExcelUtil<Agent> util = new ExcelUtil<Agent>(Agent.class);
        util.exportExcel(response, list, "代理店管理数据");
    }

    /**
     * 获取代理店管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:agent:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(agentService.selectAgentById(id));
    }

    /**
     * 新增代理店管理
     */
    @PreAuthorize("@ss.hasPermi('system:agent:add')")
    @Log(title = "代理店管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Agent agent)
    {
        return toAjax(agentService.insertAgent(agent));
    }

    /**
     * 修改代理店管理
     */
    @PreAuthorize("@ss.hasPermi('system:agent:edit')")
    @Log(title = "代理店管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Agent agent)
    {
        return toAjax(agentService.updateAgent(agent));
    }

    /**
     * 删除代理店管理
     */
    @PreAuthorize("@ss.hasPermi('system:agent:remove')")
    @Log(title = "代理店管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(agentService.deleteAgentByIds(ids));
    }
}
