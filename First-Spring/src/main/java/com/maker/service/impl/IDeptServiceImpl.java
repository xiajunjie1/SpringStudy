package com.maker.service.impl;

import com.maker.dao.DeptDao;
import com.maker.service.IDeptService;
import com.maker.vo.Dept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IDeptServiceImpl implements IDeptService {
    @Autowired
    private DeptDao dao;
    @Override
    public Dept getDept(Long deptno) {
        return dao.findById(deptno);
    }
}
