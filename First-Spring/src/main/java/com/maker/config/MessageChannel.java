package com.maker.config;

public class MessageChannel {
    private String host;
    private String token;

    public void setHost(String host) {
        this.host = host;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getHost() {
        return host;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "【channel信息】host="+this.host+"、token="+this.token;
    }
}
