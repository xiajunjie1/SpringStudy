package com.maker.aware;

import org.springframework.stereotype.Component;

@Component
public class GetDatabaseConfig implements IDatabaseConfigAware{
    private DatabaseConfig databaseConfig;

    @Override
    public void setDatabaseConfig(DatabaseConfig config) {
        this.databaseConfig=config;
    }

    public DatabaseConfig getConfig() {
        return databaseConfig;
    }
}
