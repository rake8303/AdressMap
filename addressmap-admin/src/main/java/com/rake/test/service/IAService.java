package com.rake.test.service;

import java.util.List;
import com.rake.test.domain.A;

/**
 * testService接口
 * 
 * @author rake
 * @date 2025-08-17
 */
public interface IAService 
{
    /**
     * 查询test
     * 
     * @param id test主键
     * @return test
     */
    public A selectAById(Long id);

    /**
     * 查询test列表
     * 
     * @param a test
     * @return test集合
     */
    public List<A> selectAList(A a);

    /**
     * 新增test
     * 
     * @param a test
     * @return 结果
     */
    public int insertA(A a);

    /**
     * 修改test
     * 
     * @param a test
     * @return 结果
     */
    public int updateA(A a);

    /**
     * 批量删除test
     * 
     * @param ids 需要删除的test主键集合
     * @return 结果
     */
    public int deleteAByIds(Long[] ids);

    /**
     * 删除test信息
     * 
     * @param id test主键
     * @return 结果
     */
    public int deleteAById(Long id);
}
