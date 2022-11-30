package com.maker.dao;

import com.maker.po.Company;
import org.springframework.data.repository.RepositoryDefinition;

import java.util.List;
import java.util.Set;

/**
 * 使用Repository方法映射的方式，定义dao层
 * 在定义方法名称时，根据官方提供的关键字规则，就可以自动实现各种条件查询
 * */
@RepositoryDefinition(domainClass = Company.class,idClass = Long.class)
public interface ICompanyDao2 {
    //根据cid查询Company对象，也可以写成findByCidIs/findByCidEquals
    public Company findByCid(Long cid);
    //查找注册资金大于一定金额的Company数据
    public List<Company> findByCapitalGreaterThan(Double capital);
    //查询一定范围内的cid
    public List<Company> findByCidIn(Set<Long> ids);
    //根据名称包含的关键字进行模糊查询，并且按照注册地降序排列
    public List<Company> findByNameContainingOrderByPlaceDesc(String key);
    //根据名称和地点进行模糊查询（and），并且按照地点降序排列
    public List<Company> findByNameContainingAndPlaceContainingOrderByPlaceDesc(String name,String place);
}
