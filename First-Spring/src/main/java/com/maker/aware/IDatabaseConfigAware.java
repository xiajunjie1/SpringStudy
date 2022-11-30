package com.maker.aware;

import org.springframework.beans.factory.Aware;

public interface IDatabaseConfigAware extends Aware {
    public abstract void setDatabaseConfig(DatabaseConfig config);
}
