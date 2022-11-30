package com.maker.po;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;
@Entity
public class Jpaemp implements Serializable {
    @Id
    private Long empno;
    private String ename;
    private Double sal;
    @ManyToOne
    @JoinColumn(name = "deptno")//定义关联字段（外键），如果属性和表字段名称相同，写一个就可以了
    private Jpadept dept;
    public Jpaemp(){}
    public Jpaemp(Long empno, String ename, Double sal, Jpadept dept) {
        this.empno = empno;
        this.ename = ename;
        this.sal = sal;
        this.dept = dept;
    }

    public Long getEmpno() {
        return empno;
    }

    public void setEmpno(Long empno) {
        this.empno = empno;
    }

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public Double getSal() {
        return sal;
    }

    public void setSal(Double sal) {
        this.sal = sal;
    }

    public Jpadept getDept() {
        return dept;
    }

    public void setDept(Jpadept dept) {
        this.dept = dept;
    }
}
