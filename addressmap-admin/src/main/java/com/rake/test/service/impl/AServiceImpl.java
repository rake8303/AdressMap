package com.rake.test.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.rake.test.mapper.AMapper;
import com.rake.test.domain.A;
import com.rake.test.service.IAService;

/**
 * testService业务层处理
 * 
 * @author rake
 * @date 2025-08-17
 */
@Service
public class AServiceImpl implements IAService 
{
    @Autowired
    private AMapper aMapper;

    /**
     * 查询test
     * 
     * @param id test主键
     * @return test
     */
    @Override
    public A selectAById(Long id)
    {
        return aMapper.selectAById(id);
    }

    /**
     * 查询test列表
     * 
     * @param a test
     * @return test
     */
    @Override
    public List<A> selectAList(A a)
    {
        return aMapper.selectAList(a);
    }

    /**
     * 新增test
     * 
     * @param a test
     * @return 结果
     */
    @Override
    public int insertA(A a)
    {
        return aMapper.insertA(a);
    }

    /**
     * 修改test
     * 
     * @param a test
     * @return 结果
     */
    @Override
    public int updateA(A a)
    {
        return aMapper.updateA(a);
    }

    /**
     * 批量删除test
     * 
     * @param ids 需要删除的test主键
     * @return 结果
     */
    @Override
    public int deleteAByIds(Long[] ids)
    {
        return aMapper.deleteAByIds(ids);
    }

    /**
     * 删除test信息
     * 
     * @param id test主键
     * @return 结果
     */
    @Override
    public int deleteAById(Long id)
    {
        return aMapper.deleteAById(id);
    }
}
