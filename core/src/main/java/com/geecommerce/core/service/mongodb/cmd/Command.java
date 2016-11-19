package com.geecommerce.core.service.mongodb.cmd;

import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.api.Model;
import com.mongodb.DBObject;

public interface Command {
    public boolean isOwner(String key, Object value);

    public void process(Class<? extends Model> modelClass, String originalKey, String columnName, Object value, DBObject query, QueryOptions queryOptions);
}
