package com.maker.dao;

import com.maker.po.Company;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

//@RepositoryDefinition(domainClass = Company.class,idClass = Long.class)
public interface ICompanyDao extends Repository<Company,Long> {
    public Company save(Company company);
    public List<Company> findAll();

    @Modifying(clearAutomatically = true)//刷新缓存
    @Query("Update Company As c set c.capital=:#{#param.capital},c.num=:#{#param.num} where c.cid=:#{#param.cid}")//配合SpEL编写JPQL语句
    @Transactional()
    public int editBase(@Param("param") Company company);//给参数取名称，方便SpEL引用


    @Query("Select c from Company As c where c.cid=:pid")
    public Company findById(@Param("pid") Long id);

    @Query("Select c from Company As c where c.cid in :pids")
    public List<Company> findByIds(@Param("pids") Set<Long> ids);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("delete From Company As c where c.cid=:#{[0]}")//利用索引的方式获取参数值
    public int removeById(Long id);
}
