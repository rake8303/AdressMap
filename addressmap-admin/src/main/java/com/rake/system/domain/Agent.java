package com.rake.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.rake.common.annotation.Excel;
import com.rake.common.core.domain.BaseEntity;

/**
 * 代理店管理对象 tab_agent
 * 
 * @author rake
 * @date 2025-09-19
 */
public class Agent extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 自增ID */
    private String id;

    /** 总店名称 */
    @Excel(name = "总店名称")
    private String hqName;

    /** 支店名 */
    @Excel(name = "支店名")
    private String branchName;

    /** 住所 */
    @Excel(name = "住所")
    private String address;

    /** 区域 */
    @Excel(name = "区域")
    private String region;

    /** 创建时间 */
    private Date createdAt;

    /** 更新时间 */
    private Date updatedAt;

    /** 总店简称 */
    private String abbreviation;
    private String lat;
    private String lng;
    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }



    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getId() 
    {
        return id;
    }
    public void setHqName(String hqName) 
    {
        this.hqName = hqName;
    }

    public String getHqName() 
    {
        return hqName;
    }
    public void setBranchName(String branchName) 
    {
        this.branchName = branchName;
    }

    public String getBranchName() 
    {
        return branchName;
    }
    public void setAddress(String address) 
    {
        this.address = address;
    }

    public String getAddress() 
    {
        return address;
    }
    public void setRegion(String region) 
    {
        this.region = region;
    }

    public String getRegion() 
    {
        return region;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("hqName", getHqName())
            .append("branchName", getBranchName())
            .append("address", getAddress())
            .append("region", getRegion())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .append("abbreviation", getAbbreviation())
            .toString();
    }
}
