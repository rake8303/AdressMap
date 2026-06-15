package com.rake.system.domain;

import java.util.List;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.rake.common.annotation.Excel;
import com.rake.common.core.domain.BaseEntity;

/**
 * 販売店管理对象 tab_outlet
 *
 * @author rake
 * @date 2025-09-24
 */
public class TabOutlet extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private String id;

    /** 会社名 */
    @Excel(name = "会社名")
    private String jpCompanyName;

    /** 会社名简称 */
    private String shortCompanyName;

    /** エリア */
    @Excel(name = "エリア")
    private String region;

    /** 住所（本社） */
    @Excel(name = "住所")
    private String headquartersAddress;

    /** 责任人 */
    private String contactPerson;

    /** 代理店 */
    private List<String> agentList;

    /** 月次販売台数(potensial) */
    private String totalSalesAvg;

    /** 突破状況 */
    @Excel(name = "突破状況")
    private String breakthroughStatus;

    /** 販売実績 */
    private String hwSalesAvg;

    /** 台数増の見込み、対策など */
    private String estimatedIncrease;

    /** 販売計画 */
    private String salesPlan;

    /** 備考 */
    private String remarks;

    /** 安装商大会参加情况 */
    private String installerEventParticipation;


    private String lat;
    private String lng;

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }


    /** 创建时间 */
    private Date createdAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updatedAt;

    /** 简称 */
    private String abbreviation;

    /** 会社種類 */
    private String cpnType;

    /** 販売店備考 */
    private String outletRemark;

    /** 販売実績レベル */
    private String salesRecord;

    /** 販売店管理-历史记录信息 */
    private List<OutletHistory> outletHistoryList;

    private List<OutletMonthlySales> monthlySalesList;

    public void setId(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }
    public void setJpCompanyName(String jpCompanyName)
    {
        this.jpCompanyName = jpCompanyName;
    }

    public String getJpCompanyName()
    {
        return jpCompanyName;
    }
    public void setShortCompanyName(String shortCompanyName)
    {
        this.shortCompanyName = shortCompanyName;
    }

    public String getShortCompanyName()
    {
        return shortCompanyName;
    }
    public void setRegion(String region)
    {
        this.region = region;
    }

    public String getRegion()
    {
        return region;
    }
    public void setHeadquartersAddress(String headquartersAddress)
    {
        this.headquartersAddress = headquartersAddress;
    }

    public String getHeadquartersAddress()
    {
        return headquartersAddress;
    }
    public void setContactPerson(String contactPerson)
    {
        this.contactPerson = contactPerson;
    }

    public String getContactPerson()
    {
        return contactPerson;
    }
    public List<String> getAgentList()
    {
        return agentList;
    }

    public void setAgentList(List<String> agentList)
    {
        this.agentList = agentList;
    }
    public void setTotalSalesAvg(String totalSalesAvg)
    {
        this.totalSalesAvg = totalSalesAvg;
    }

    public String getTotalSalesAvg()
    {
        return totalSalesAvg;
    }
    public void setBreakthroughStatus(String breakthroughStatus)
    {
        this.breakthroughStatus = breakthroughStatus;
    }

    public String getBreakthroughStatus()
    {
        return breakthroughStatus;
    }
    public void setHwSalesAvg(String hwSalesAvg)
    {
        this.hwSalesAvg = hwSalesAvg;
    }

    public String getHwSalesAvg()
    {
        return hwSalesAvg;
    }
    public void setEstimatedIncrease(String estimatedIncrease)
    {
        this.estimatedIncrease = estimatedIncrease;
    }

    public String getEstimatedIncrease()
    {
        return estimatedIncrease;
    }
    public void setSalesPlan(String salesPlan)
    {
        this.salesPlan = salesPlan;
    }

    public String getSalesPlan()
    {
        return salesPlan;
    }
    public void setRemarks(String remarks)
    {
        this.remarks = remarks;
    }

    public String getRemarks()
    {
        return remarks;
    }
    public void setInstallerEventParticipation(String installerEventParticipation)
    {
        this.installerEventParticipation = installerEventParticipation;
    }

    public String getInstallerEventParticipation()
    {
        return installerEventParticipation;
    }
    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }
    public void setUpdatedAt(Date updatedAt)
    {
        this.updatedAt = updatedAt;
    }

    public Date getUpdatedAt()
    {
        return updatedAt;
    }
    public void setAbbreviation(String abbreviation)
    {
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation()
    {
        return abbreviation;
    }
    public void setCpnType(String cpnType)
    {
        this.cpnType = cpnType;
    }

    public String getCpnType()
    {
        return cpnType;
    }
    public void setOutletRemark(String outletRemark)
    {
        this.outletRemark = outletRemark;
    }

    public String getOutletRemark()
    {
        return outletRemark;
    }
    public void setSalesRecord(String salesRecord)
    {
        this.salesRecord = salesRecord;
    }

    public String getSalesRecord()
    {
        return salesRecord;
    }

    public List<OutletHistory> getOutletHistoryList()
    {
        return outletHistoryList;
    }

    public void setOutletHistoryList(List<OutletHistory> outletHistoryList)
    {
        this.outletHistoryList = outletHistoryList;
    }

    public List<OutletMonthlySales> getMonthlySalesList()
    {
        return monthlySalesList;
    }

    public void setMonthlySalesList(List<OutletMonthlySales> monthlySalesList)
    {
        this.monthlySalesList = monthlySalesList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("jpCompanyName", getJpCompanyName())
            .append("shortCompanyName", getShortCompanyName())
            .append("region", getRegion())
            .append("headquartersAddress", getHeadquartersAddress())
            .append("contactPerson", getContactPerson())
            .append("agentList", getAgentList())
            .append("totalSalesAvg", getTotalSalesAvg())
            .append("breakthroughStatus", getBreakthroughStatus())
            .append("hwSalesAvg", getHwSalesAvg())
            .append("estimatedIncrease", getEstimatedIncrease())
            .append("salesPlan", getSalesPlan())
            .append("remarks", getRemarks())
            .append("installerEventParticipation", getInstallerEventParticipation())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .append("abbreviation", getAbbreviation())
            .append("cpnType", getCpnType())
            .append("outletRemark", getOutletRemark())
            .append("salesRecord", getSalesRecord())
            .append("outletHistoryList", getOutletHistoryList())
            .append("monthlySalesList", getMonthlySalesList())
            .toString();
    }
}
