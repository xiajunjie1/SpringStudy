package com.maker.vo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Set;
@Component
@PropertySource("classpath:config/Book.properties")//指定资源文件位置
public class Book_SpEL {
    @Value("#{'${Book.name}'.toUpperCase()}")
    private String name;
    @Value("#{${Book.price} }")
    private Double price;
    @Value("#{'${Book.sets}'.split(';') }")
    private Set<String> sets;

    public Book_SpEL(){}

    public Book_SpEL(String name,Double price){
        this.name=name;
        this.price=price;
    }
    @Override
    public String toString() {
        return this.name+"（"+this.price+"）"+"-----"+sets;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public static Double getCount(Integer count){
        return 66.66*count;
    }
}
