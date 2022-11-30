package com.maker.vo;

public class Message {
    private String path;

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "【路径为】"+this.path;
    }
}
