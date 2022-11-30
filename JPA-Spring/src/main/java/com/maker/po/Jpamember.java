package com.maker.po;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

@Entity
public class Jpamember implements Serializable {
    @Id
    private String mid;
    private String password;
    @ManyToMany
    @JoinTable(//指定关联表
            name="member_role",//关联表名称
            joinColumns = {@JoinColumn(name="mid")},//jpamember和member_role之间的联系
            inverseJoinColumns = {@JoinColumn(name="rid")}//通过jpamember找到jparole的字段
    )
    private List<Jparole> roles;

    public Jpamember(){}

    public Jpamember(String mid, String password, List<Jparole> roles) {
        this.mid = mid;
        this.password = password;
        this.roles = roles;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Jparole> getRoles() {
        return roles;
    }

    public void setRoles(List<Jparole> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "memberID："+this.mid+"、password："+this.password;
    }
}
