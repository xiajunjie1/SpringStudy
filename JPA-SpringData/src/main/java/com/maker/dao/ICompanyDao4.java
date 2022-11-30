package com.maker.dao;

import com.maker.po.Company;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ICompanyDao4 extends CrudRepository<Company,Long>, PagingAndSortingRepository<Company,Long> {
}
