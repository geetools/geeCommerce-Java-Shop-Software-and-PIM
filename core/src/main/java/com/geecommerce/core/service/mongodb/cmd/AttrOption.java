package com.geecommerce.core.service.mongodb.cmd;

import java.util.ArrayList;
import java.util.List;

import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.api.Model;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class AttrOption extends AbstractCommand {
    private static final String CMD_PREFIX = "$opt.";
    private static final String COL_ATTRIBUTES = "attributes";
    private static final String FIELD_OPTIONS = "opt_id";
    private static final String ALL = "$all";
    private static final String ELEM_MATCH = "$elemMatch";

    @Override
    public boolean isOwner(String key, Object value) {
        return key.startsWith(CMD_PREFIX);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void process(Class<? extends Model> modelClass, String originalKey, String columnName, Object value,
        DBObject query, QueryOptions queryOptions) {
        DBObject dboAttributes = (DBObject) query.get(COL_ATTRIBUTES);

        // ---------------------------------------------------------------
        // As this may not be the first command to set an attribute query,
        // we make sure that we always add to an existing list.
        // ---------------------------------------------------------------
        if (dboAttributes == null) {
            dboAttributes = new BasicDBObject(ALL, new ArrayList<>());
            query.put(COL_ATTRIBUTES, dboAttributes);
        }

        List<DBObject> allAttributes = (List<DBObject>) dboAttributes.get(ALL);

        allAttributes.add(new BasicDBObject(ELEM_MATCH, new BasicDBObject(FIELD_OPTIONS, value)));
    }
}
