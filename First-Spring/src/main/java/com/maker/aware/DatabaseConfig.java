package com.maker.aware;

public class DatabaseConfig {
    private String name;
    private String url;

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "【DatabaseConfig】name="+this.name+"、url="+this.url;
    }
}
