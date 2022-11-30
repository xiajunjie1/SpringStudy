package com.maker.po;

import jakarta.persistence.*;

import java.io.Serializable;
@Entity
public class Details implements Serializable {
    @Id
    private String uid;
    private String name;
    private Integer age;
    @OneToOne
    @JoinColumn( //进行关联字段的配置
            name="uid",//与Login类之间关联属性的配置（外键）
            referencedColumnName = "uid",//数据库表字段（外键）
            unique = true
    )
    private Login login;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    @Override
    public String toString() {
        return "【Details类】uid="+this.uid+"、name="+this.name+"、age="+this.age;
    }
}
