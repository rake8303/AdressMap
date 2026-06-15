package com.rake.outletagentunion.controller;

import com.rake.common.core.domain.AjaxResult;
import com.rake.outletagentunion.domain.OutletAgentUnion;
import com.rake.outletagentunion.service.IOutletAgentUnionService;
import com.rake.utils.AgentRoleUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;

@RestController
@RequestMapping("/outletAgentUnion")
@Api(tags = "販売店商流")
public class OutletAgentUnionController
{
    private final IOutletAgentUnionService service;

    public OutletAgentUnionController(IOutletAgentUnionService service)
    {
        this.service = service;
    }

    @GetMapping("/list")
    @ApiOperation("查询販売店商流列表")
    @PermitAll
    public AjaxResult list()
    {
        return AjaxResult.success(service.selectOutletAgentUnionList());
    }

    @GetMapping("/listbybusinessflow")
    @ApiOperation("按登录角色商流查询販売店列表")
    @PermitAll
    public AjaxResult listByBusinessflow(Authentication authentication)
    {
        List<String> businessflows = AgentRoleUtil.getAgentRoleNames(authentication);
        if (businessflows.isEmpty())
        {
            return AjaxResult.success(service.selectOutletAgentUnionList());
        }
        return AjaxResult.success(service.selectOutletAgentUnionListByBusinessflow(businessflows));
    }
}
