package com.maker.resource;

import org.springframework.core.io.Resource;

public class MessageResource {
    private Resource resource;

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Resource getResource() {
        return resource;
    }
}
