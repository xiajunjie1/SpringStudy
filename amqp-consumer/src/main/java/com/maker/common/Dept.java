package com.maker.common;

import java.io.Serializable;
/**
 * 生产消费者之间传输对象
 * 对象本身必须实现序列化接口
 *
 * */
public class Dept implements Serializable {
    private Long deptno;
    private String dname;
    private String loc;

    public Long getDeptno() {
        return deptno;
    }

    public void setDeptno(Long deptno) {
        this.deptno = deptno;
    }

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }
}
