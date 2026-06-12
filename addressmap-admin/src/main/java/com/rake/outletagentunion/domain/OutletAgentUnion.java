package com.rake.outletagentunion.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OutletAgentUnion 实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OutletAgentUnion {

    // 基本信息
    private Long id;
    private String jpCompanyName;
    private String shortCompanyName;
    private String abbreviation;
    private String region;
    private String headquartersAddress;
    private String contactPerson;
    private String branchName;
    private String companyType;

    // 销售数据
    private String totalSalesAvg;
    private String hwSalesAvg;
    private String estimatedIncrease;

    // 状态和计划
    private String breakthroughStatus;
    private String salesPlan;

    // 其他信息
    private String remarks;
    private String installerEventParticipation;
    private String businessflow;
    private String cpnType;
    private String salesRecord;
    private String lat;
    private String lng;
}
