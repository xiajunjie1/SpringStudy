package com.maker.di.config;

public class MessageConfig_Profile {
    private String hostname;
    private int port;
    private boolean enable;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public String toString() {
        return this.hostname+":"+this.port+"("+this.enable+")";
    }
}
