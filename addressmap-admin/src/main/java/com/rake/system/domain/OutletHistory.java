package com.rake.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.rake.common.annotation.Excel;
import com.rake.common.core.domain.BaseEntity;

/**
 * 販売店管理-历史记录对象 tab_outlet_history
 *
 * @author rake
 * @date 2025-09-24
 */
public class OutletHistory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 子表主键 */
    private String historyId;

    /** 关联主表ID */
    @Excel(name = "关联主表ID")
    private String outletId;

    /** 创建人 */
    @Excel(name = "创建人")
    private String createdBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm")
    private Date createdAt;

    /** 修改人 */
    @Excel(name = "修改人")
    private String updatedBy;

    /** 修改时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Excel(name = "修改时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm")
    private Date updatedAt;

    /** 代理店 */
    @Excel(name = "代理店")
    private String agent;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    /** 代理店 */
    @Excel(name = "remark")
    private String remark;



    public void setHistoryId(String historyId)
    {
        this.historyId = historyId;
    }

    public String getHistoryId()
    {
        return historyId;
    }
    public void setOutletId(String outletId)
    {
        this.outletId = outletId;
    }

    public String getOutletId()
    {
        return outletId;
    }
    public void setCreatedBy(String createdBy)
    {
        this.createdBy = createdBy;
    }

    public String getCreatedBy()
    {
        return createdBy;
    }
    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }
    public void setUpdatedBy(String updatedBy)
    {
        this.updatedBy = updatedBy;
    }

    public String getUpdatedBy()
    {
        return updatedBy;
    }
    public void setUpdatedAt(Date updatedAt)
    {
        this.updatedAt = updatedAt;
    }

    public Date getUpdatedAt()
    {
        return updatedAt;
    }
    public void setAgent(String agent)
    {
        this.agent = agent;
    }

    public String getAgent()
    {
        return agent;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("historyId", getHistoryId())
            .append("outletId", getOutletId())
            .append("createdBy", getCreatedBy())
            .append("createdAt", getCreatedAt())
            .append("updatedBy", getUpdatedBy())
            .append("updatedAt", getUpdatedAt())
            .append("remark", getRemark())
            .append("agent", getAgent())
            .toString();
    }
}
