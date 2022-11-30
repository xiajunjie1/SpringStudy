package com.maker.po;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import java.io.Serializable;
import java.util.List;

@Entity
public class Jparole implements Serializable {
    @Id
    private String rid;
    private String name;
    @ManyToMany//多对多的数据关联
    private List<Jpamember> members;

    public Jparole() {
    }

    public Jparole(String rid) {
        this.rid = rid;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Jpamember> getMembers() {
        return members;
    }

    public void setMembers(List<Jpamember> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "RoleID："+this.rid+"、name："+this.name;
    }
}
