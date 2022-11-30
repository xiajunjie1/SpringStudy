package com.maker.service.impl;

import com.maker.dao.IEmpDao;
import com.maker.po.Emp;
import com.maker.service.IEmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmpServiceImpl implements IEmpService {
    @Autowired
    private IEmpDao dao;
    @Override
    public Emp edit(Emp emp) {
        return dao.save(emp);

    }

    @Override
    public boolean delete(String eid) {
       if(dao.existsById(eid)){
           dao.deleteById(eid);
           return true;
       }
        return false;
    }

    @Override
    public Emp getById(String eid) {
        Optional<Emp> result=dao.findById(eid);
        if(result.isPresent()){
            return result.get();
        }
        return null;
    }

    @Override
    public Emp getByEname(String ename) {

        return dao.findByEname(ename).get(0);

    }

    @Override
    public Emp getByEname2(Emp emp) {
        return dao.findByEname(emp.getEname()).get(0);
    }
}
