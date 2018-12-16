package com.pyg.sellergoods.service;

import com.pyg.pojo.TbBrand;
import entity.PageResult;

import java.util.List;
import java.util.Map;

/**
 *品牌接口
 */
public interface BrandService {
    //查询所有
    public List<TbBrand> findAll();
    //查询所有之分页查询
    public PageResult findPage(int pageNum , int pageSize);
    //添加
    public void add(TbBrand tbBrand);
    //修改的查询回现
    public TbBrand findOne(Long id);
    //修改
    public  void update(TbBrand tbBrand);
    //批量删除
    public void delete(Long[] ids);
    //品牌条件查询
    public PageResult findPage(TbBrand tbBrand,int pageNum,int pageSize);

    /**
     * 品牌下拉框数据
     */
    List<Map> selectOptionList();
}
