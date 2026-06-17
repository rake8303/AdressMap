package com.rake.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rake.common.annotation.Excel;
import com.rake.common.core.domain.BaseEntity;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class OutletMonthlySales extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long salesId;

    @Excel(name = "Outlet ID")
    private String outletId;

    @Excel(name = "Sales Month")
    private String salesMonth;

    @Excel(name = "Quantity")
    private Integer quantity;

    @Excel(name = "Product Name")
    private String productName;

    @Excel(name = "Remark")
    private String remark;

    @Excel(name = "Updated By")
    private String updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Excel(name = "Updated At", width = 30, dateFormat = "yyyy-MM-dd HH:mm")
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
    public String toString()
    {
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
