package com.maker.di.config;

public class MessageConfig_copy {
    private String hostname;
    private int port;
    private boolean enable;

    public MessageConfig_copy(String hostname,int port,boolean enable){
        this.hostname=hostname;
        this.port=port;
        this.enable=enable;
    }

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
        return this.hostname+":"+this.port+"----"+this.enable;
    }
}
