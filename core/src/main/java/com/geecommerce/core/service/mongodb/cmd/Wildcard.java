package com.geecommerce.core.service.mongodb.cmd;

import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.api.Model;
import com.mongodb.DBObject;

public class Wildcard extends AbstractCommand {
    protected static final String CMD_WILDCARD = "$wc:";
    protected static final String REGEX_WC = ".*";
    protected static final String ASTERIX = "*";
    protected static final Character CARET = '^';

    @Override
    public boolean isOwner(String key, Object value) {
        if (value == null || !(value instanceof String))
            return false;

        return ((String) value).startsWith(CMD_WILDCARD);
    }

    @Override
    public void process(Class<? extends Model> modelClass, String originalKey, String columnName, Object value,
        DBObject query, QueryOptions queryOptions) {
        String rawValue = ((String) value).substring(7);

        if (rawValue.indexOf(ASTERIX) > 2) {
            query.put(columnName, toWildcardPattern(rawValue));
        } else {
            query.put(columnName, value);
        }
    }
}
