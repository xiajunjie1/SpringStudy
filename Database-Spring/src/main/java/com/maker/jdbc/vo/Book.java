package com.maker.jdbc.vo;

public class Book {
    private Long bid;
    private String title;
    private String author;
    private Double price;

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setBid(Long bid) {
        this.bid = bid;
    }

    public Long getBid() {
        return bid;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public Double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "【图书信息】bid="+this.bid+"、title="+this.title+"、author="+this.author+"、price="+this.price;
    }
}
