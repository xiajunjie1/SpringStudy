package com.maker.po;

import jakarta.persistence.*;

import java.io.Serializable;
/**
 * 一对一关系
 * */
@Entity
public class Login implements Serializable {
    @Id
    private String uid;
    private String password;
    @OneToOne(//设置一对一关联
            mappedBy = "login",//映射Details类中的属性login
            cascade = CascadeType.ALL,//设置级联关系
            fetch = FetchType.EAGER //抓取全部数据，默认一般用LAZY懒加载
    )
    private Details details;

    public String getUid() {
        return uid;
    }

    public String getPassword() {
        return password;
    }

    public Details getDetails() {
        return details;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDetails(Details details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "【Login类】uid="+this.uid+"password="+this.password;
    }
}
