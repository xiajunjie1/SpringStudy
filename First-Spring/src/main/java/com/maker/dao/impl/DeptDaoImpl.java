package com.maker.dao.impl;

import com.maker.dao.DeptDao;
import com.maker.vo.Dept;
import org.springframework.stereotype.Repository;

@Repository
public class DeptDaoImpl implements DeptDao {
    @Override
    public Dept findById(Long deptno) {
        //模拟查询数据库后得到结果
        Dept dept=new Dept();
        dept.setDeptno(deptno);
        dept.setDeptname("jayjxia-"+deptno);
        dept.setLoc("武汉");
        return dept;
    }

    @Override
    public String toString() {
        return "【"+super.hashCode()+"】DeptDao实现子类";
    }
}
