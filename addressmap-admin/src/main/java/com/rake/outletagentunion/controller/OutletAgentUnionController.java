package com.rake.outletagentunion.controller;

import com.rake.common.core.domain.AjaxResult;
import com.rake.outletagentunion.domain.OutletAgentUnion;
import com.rake.outletagentunion.service.IOutletAgentUnionService;
import com.rake.utils.AgentRoleUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;




@RestController
@RequestMapping("/outletAgentUnion")
@Api(tags = "网点代理联盟管理")
public class OutletAgentUnionController {
    private final IOutletAgentUnionService service;

    public OutletAgentUnionController(IOutletAgentUnionService service) {
        this.service = service;
    }

    @GetMapping("/list")
    @ApiOperation("获取网点代理联盟列表")
    @PermitAll
    public AjaxResult list() {
        List<OutletAgentUnion> list = service.selectOutletAgentUnionList();
        // 过滤掉实体中属性为空的值
        List<OutletAgentUnion> filteredList = list.stream()
                .filter(Objects::nonNull) // 过滤掉null值实体
                .peek(this::filterEmptyProperties)
                .collect(Collectors.toList());
        return AjaxResult.success(filteredList);
    }



    @GetMapping("/listbybusinessflow")
    @ApiOperation("获取网点代理联盟列表")
    @PermitAll
    public AjaxResult listbybusinessflow(Authentication authentication) {
        List<String> roleNames = AgentRoleUtil.getAgentRoleNames(authentication);
//        System.out.println(roleNames.get(0));
        List<OutletAgentUnion> list = roleNames.isEmpty()
                ? Collections.emptyList()
                : service.selectOutletAgentUnionListByBusinessflow(roleNames);
        // 过滤掉实体中属性为空的值
        List<OutletAgentUnion> filteredList = list.stream()
                .filter(Objects::nonNull) // 过滤掉null值实体
                .peek(this::filterEmptyProperties)
                .collect(Collectors.toList());
        return AjaxResult.success(filteredList);
    }
    // 辅助方法：过滤实体中的空属性
// 辅助方法：过滤实体中的空属性
    private void filterEmptyProperties(OutletAgentUnion outlet) {
        if (outlet.getJpCompanyName() != null && outlet.getJpCompanyName().trim().isEmpty()) {
            outlet.setJpCompanyName(null);
        }
        if (outlet.getShortCompanyName() != null && outlet.getShortCompanyName().trim().isEmpty()) {
            outlet.setShortCompanyName(null);
        }
        if (outlet.getRegion() != null && outlet.getRegion().trim().isEmpty()) {
            outlet.setRegion(null);
        }
        if (outlet.getHeadquartersAddress() != null && outlet.getHeadquartersAddress().trim().isEmpty()) {
            outlet.setHeadquartersAddress(null);
        }
        if (outlet.getContactPerson() != null && outlet.getContactPerson().trim().isEmpty()) {
            outlet.setContactPerson(null);
        }
        if (outlet.getXsolSales() != null && outlet.getXsolSales().trim().isEmpty()) {
            outlet.setXsolSales(null);
        }
        if (outlet.getDmmSales() != null && outlet.getDmmSales().trim().isEmpty()) {
            outlet.setDmmSales(null);
        }
        if (outlet.getWwbSales() != null && outlet.getWwbSales().trim().isEmpty()) {
            outlet.setWwbSales(null);
        }
        if (outlet.getTakashimaSales() != null && outlet.getTakashimaSales().trim().isEmpty()) {
            outlet.setTakashimaSales(null);
        }
        if (outlet.getHanhwaSales() != null && outlet.getHanhwaSales().trim().isEmpty()) {
            outlet.setHanhwaSales(null);
        }
//        if (outlet.getTotalSalesAvg() != null && outlet.getTotalSalesAvg().trim().isEmpty()) {
//            outlet.setTotalSalesAvg(null);
//        }
        if (outlet.getBreakthroughStatus() != null && outlet.getBreakthroughStatus().trim().isEmpty()) {
            outlet.setBreakthroughStatus(null);
        }
//        if (outlet.getHwSalesAvg() != null && outlet.getHwSalesAvg().trim().isEmpty()) {
//            outlet.setHwSalesAvg(null);
//        }
        if (outlet.getEstimatedIncrease() != null && outlet.getEstimatedIncrease().trim().isEmpty()) {
            outlet.setEstimatedIncrease(null);
        }
        if (outlet.getSalesPlan() != null && outlet.getSalesPlan().trim().isEmpty()) {
            outlet.setSalesPlan(null);
        }
        if (outlet.getRemarks() != null && outlet.getRemarks().trim().isEmpty()) {
            outlet.setRemarks(null);
        }
        if (outlet.getInstallerEventParticipation() != null && outlet.getInstallerEventParticipation().trim().isEmpty()) {
            outlet.setInstallerEventParticipation(null);
        }
        if (outlet.getBranchName() != null && outlet.getBranchName().trim().isEmpty()) {
            outlet.setBranchName(null);
        }
    }


}
