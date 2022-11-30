package com.maker.dao;

import com.maker.po.Emp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IEmpDao extends JpaRepository<Emp,String> {
    public List<Emp> findByEname(String ename);
}
