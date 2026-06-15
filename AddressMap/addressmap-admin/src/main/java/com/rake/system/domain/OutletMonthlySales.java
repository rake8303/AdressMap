package com.rake.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.rake.common.annotation.Excel;
import com.rake.common.core.domain.BaseEntity;

/**
 * Monthly sales record for an outlet.
 */
public class OutletMonthlySales extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long salesId;

    /** Outlet ID */
    @Excel(name = "販売店ID")
    private String outletId;

    /** Sales month, format yyyy-MM */
    @Excel(name = "年月")
    private String salesMonth;

    /** Monthly sales quantity */
    @Excel(name = "月次販売台数")
    private Integer quantity;

    /** Product name */
    @Excel(name = "商品名称")
    private String productName;

    /** Remark */
    @Excel(name = "備考")
    private String remark;

    /** Last updated by */
    @Excel(name = "最后修改人")
    private String updatedBy;

    /** Last updated time */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Excel(name = "最后修改时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm")
    private Date updatedAt;

    public Long getSalesId()
    {
        return salesId;
    }

    public void setSalesId(Long salesId)
    {
        this.salesId = salesId;
    }

    public String getOutletId()
    {
        return outletId;
    }

    public void setOutletId(String outletId)
    {
        this.outletId = outletId;
    }

    public String getSalesMonth()
    {
        return salesMonth;
    }

    public void setSalesMonth(String salesMonth)
    {
        this.salesMonth = salesMonth;
    }

    public Integer getQuantity()
    {
        return quantity;
    }

    public void setQuantity(Integer quantity)
    {
        this.quantity = quantity;
    }

    public String getProductName()
    {
        return productName;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getUpdatedBy()
    {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy)
    {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedAt()
    {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt)
    {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("salesId", getSalesId())
            .append("outletId", getOutletId())
            .append("salesMonth", getSalesMonth())
            .append("quantity", getQuantity())
            .append("productName", getProductName())
            .append("remark", getRemark())
            .append("updatedBy", getUpdatedBy())
            .append("updatedAt", getUpdatedAt())
            .toString();
    }
}
