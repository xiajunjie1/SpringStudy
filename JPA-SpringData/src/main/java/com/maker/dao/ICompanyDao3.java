package com.maker.dao;

import com.maker.po.Company;
import org.springframework.data.repository.CrudRepository;
/**
 * 继承了CrudRepository接口后，不需要在内部定义任何的方法
 * crud的方法已经在父接口中被定义好了
 *
 * */

public interface ICompanyDao3 extends CrudRepository<Company,Long> {

}
