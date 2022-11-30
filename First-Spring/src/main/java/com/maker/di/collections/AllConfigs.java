package com.maker.di.collections;

import com.maker.di.config.MessageConfig;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class AllConfigs {
    private List<MessageConfig> mconfigs;
    private Set<MessageConfig> msets;

    private Map<String,MessageConfig> maps;


    public List<MessageConfig> getMconfigs() {
        return mconfigs;
    }

    public void setMconfigs(List<MessageConfig> mconfigs) {
        this.mconfigs = mconfigs;
    }

    public Set<MessageConfig> getMsets() {
        return msets;
    }

    public void setMsets(Set<MessageConfig> msets) {
        this.msets = msets;
    }

    public void setMaps(Map<String, MessageConfig> maps) {
        this.maps = maps;
    }

    public Map<String, MessageConfig> getMaps() {
        return maps;
    }
}
