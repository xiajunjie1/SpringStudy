package com.maker.po;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.io.Serializable;
import java.util.List;

@Entity
public class Jpadept implements Serializable {
    @Id
    private Long deptno;
    private String dname;
    private String loc;
    @OneToMany(//映射一对多的关系
        mappedBy = "dept", //Jpaemp中的属性名称
            cascade = CascadeType.ALL

    )
    private List<Jpaemp> emps;
    public Jpadept(){}

    public Jpadept(Long deptno, String dname, String loc) {
        this.deptno = deptno;
        this.dname = dname;
        this.loc = loc;
    }



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

    public List<Jpaemp> getEmps() {
        return emps;
    }

    public void setEmps(List<Jpaemp> emps) {
        this.emps = emps;
    }
}
