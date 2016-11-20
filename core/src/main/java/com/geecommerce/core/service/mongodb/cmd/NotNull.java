package com.geecommerce.core.service.mongodb.cmd;

import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.api.Model;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class NotNull extends AbstractCommand {
    private static final String CMD_NOT_NULL = "$cb.nn";
    private static final String NOT_EQUALS = "$ne";

    @Override
    public boolean isOwner(String key, Object value) {
        if (value == null || !(value instanceof String))
            return false;

        return value.equals(CMD_NOT_NULL);
    }

    @Override
    public void process(Class<? extends Model> modelClass, String originalKey, String columnName, Object value,
        DBObject query, QueryOptions queryOptions) {
        query.put(columnName, new BasicDBObject(NOT_EQUALS, null));
    }
}
