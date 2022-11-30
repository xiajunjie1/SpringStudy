package com.maker.factory;

import com.maker.vo.Dept;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class DeptFactoryBean implements FactoryBean<Dept> {
    @Override
    public Dept getObject() throws Exception {
        Dept dept=new Dept();
        dept.setLoc("wuhan");
        dept.setDeptname("business");
        dept.setDeptno(new Random().nextLong(1000L));
        return dept;
    }

    @Override
    public Class<?> getObjectType() {
        return Dept.class;
    }

    @Override
    public boolean isSingleton() {
        return FactoryBean.super.isSingleton();
    }
}
