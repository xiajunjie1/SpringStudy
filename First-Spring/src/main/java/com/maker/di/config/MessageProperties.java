package com.maker.di.config;

import java.util.Properties;

public class MessageProperties {
    private String subject;
    private Properties attribute;

    public void setAttribute(Properties attribute) {
        this.attribute = attribute;
    }

    public Properties getAttribute() {
        return attribute;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }
}
