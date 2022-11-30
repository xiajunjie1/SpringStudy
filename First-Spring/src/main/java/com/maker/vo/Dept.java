package com.maker.vo;

public class Dept {
    private Long deptno;
    private String deptname;
    private String loc;

    public void setDeptname(String deptname) {
        this.deptname = deptname;
    }

    public void setDeptno(Long deptno) {
        this.deptno = deptno;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public Long getDeptno() {
        return deptno;
    }

    public String getDeptname() {
        return deptname;
    }

    public String getLoc() {
        return loc;
    }

    @Override
    public String toString() {
        return "【"+super.hashCode()+"】+deptno："+this.deptno+"，deptname："+this.deptname+"，Loc："+this.loc;
    }
}
