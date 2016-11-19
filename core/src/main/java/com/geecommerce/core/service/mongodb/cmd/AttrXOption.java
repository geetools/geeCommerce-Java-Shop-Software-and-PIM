package com.geecommerce.core.service.mongodb.cmd;

import java.util.ArrayList;
import java.util.List;

import com.geecommerce.core.service.AttributeSupport;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.system.attribute.Attributes;
import com.geecommerce.core.type.Id;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class AttrXOption extends AbstractCommand {
    private static final String CMD_PREFIX = "cb.xopt.";
    private static final String COL_ATTRIBUTES = "attributes";
    private static final String FIELD_ATTR_ID = "attr_id";
    private static final String FIELD_VAL = "val";
    private static final String FIELD_STORE = "s";
    private static final String FIELD_XOPT_ID = "xopt_id";
    private static final String ALL = "$all";
    private static final String ELEM_MATCH = "$elemMatch";

    @Override
    public boolean isOwner(String key, Object value) {
        return key.startsWith(CMD_PREFIX);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void process(Class<? extends Model> modelClass, String originalKey, String columnName, Object value, DBObject query, QueryOptions queryOptions) {
        if (!AttributeSupport.class.isAssignableFrom(modelClass))
            throw new IllegalStateException("The model type '" + modelClass.getName() + "' must implement the interface AttributeSupport when using the mongodb CMD 'AttrXOption'.");

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

        // ---------------------------------------------------------------
        // Build the actual attribute value query part.
        // ---------------------------------------------------------------

        String attrCode = originalKey.substring(9);

        DBObject dboVal = new BasicDBObject(FIELD_VAL, value);

        if (queryOptions != null && queryOptions.hasAttributeStoreLimitFor(attrCode)) {
            Id storeId = queryOptions.getFirstStoreLimitForAttribute(attrCode);
            dboVal.put(FIELD_STORE, storeId);
        }

        Id attributeId = Attributes.getAttributeId((Class<? extends AttributeSupport>) modelClass, attrCode);

        DBObject attrVal = new BasicDBObject(FIELD_ATTR_ID, attributeId).append(FIELD_XOPT_ID, elemMatch(dboVal));

        allAttributes.add(elemMatch(attrVal));

        System.out.println("AttrXOption: " + query);
    }

    public DBObject elemMatch(DBObject dbObject) {
        return new BasicDBObject(ELEM_MATCH, dbObject);
    }
}
