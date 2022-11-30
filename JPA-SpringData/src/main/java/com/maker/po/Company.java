package com.maker.po;

import javax.persistence.Entity;

import javax.persistence.Cacheable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * 此处都用javax包，因为jpa2.X中使用的还是javax
 * */
@Entity
@Cacheable
public class Company implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自动增长
    private Long cid;
    private String name;
    private Double capital;
    private String place;
    private Integer num;

    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getCapital() {
        return capital;
    }

    public void setCapital(Double capital) {
        this.capital = capital;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
