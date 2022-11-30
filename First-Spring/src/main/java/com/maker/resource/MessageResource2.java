package com.maker.resource;

import org.springframework.core.io.Resource;

public class MessageResource2 {
    private Resource[] resources;
    private Resource[] resources2;//用来接收jar文件的资源文件

    public void setResources(Resource[] resources) {
        this.resources = resources;
    }

    public Resource[] getResources() {
        return resources;
    }

    public void setResources2(Resource[] resources2) {
        this.resources2 = resources2;
    }

    public Resource[] getResources2() {
        return resources2;
    }
}
