package com.rake.test.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.rake.common.annotation.Excel;
import com.rake.common.core.domain.BaseEntity;

/**
 * test对象 a
 * 
 * @author rake
 * @date 2025-08-17
 */
public class A extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    /** 值 */
    @Excel(name = "值")
    private String value;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setValue(String value) 
    {
        this.value = value;
    }

    public String getValue() 
    {
        return value;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("value", getValue())
            .toString();
    }
}
