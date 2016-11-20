package com.geecommerce.core.app.standalone.helper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.BSON;
import org.bson.Transformer;
import org.bson.types.ObjectId;

import com.geecommerce.core.db.Connections;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.type.Id;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class MongoHelper {
    static {
        BSON.addEncodingHook(Id.class, new Transformer() {
            public Object transform(Object o) {
                if (o instanceof Id) {
                    return ((Id) o).longValue();
                }

                return o;
            }
        });

        BSON.addEncodingHook(BigDecimal.class, new Transformer() {
            public Object transform(Object o) {
                if (o instanceof BigDecimal) {
                    return ((BigDecimal) o).doubleValue();
                }

                return o;
            }
        });
    }

    public static Map<String, Object> findOne(DB db, String collectionName, Map<String, Object> query,
        QueryOptions queryOptions) {
        List<Map<String, Object>> results = find(db, collectionName, query, queryOptions);

        return results.size() == 0 ? null : results.get(0);
    }

    public static List<Map<String, Object>> find(DB db, String collectionName, Map<String, Object> query,
        QueryOptions queryOptions) {
        List<Map<String, Object>> results = new ArrayList<>();

        DBCollection col = db.getCollection(collectionName);

        DBObject dbQuery = new BasicDBObject();

        Set<String> keys = query.keySet();

        for (String key : keys) {
            Object value = query.get(key);

            if (value instanceof String && ObjectId.isValid((String) value)) {
                dbQuery.put(key, new ObjectId((String) query.get(key)));
            } else {
                dbQuery.put(key, query.get(key));
            }
        }

        DBCursor cursor = col.find(dbQuery);

        if (queryOptions != null) {
            if (queryOptions.sortAsc() != null && queryOptions.sortAsc().size() > 0) {
                cursor.sort(new BasicDBObject(queryOptions.sortAsc().get(0), 1));
            } else if (queryOptions.sortDesc() != null && queryOptions.sortDesc().size() > 0) {
                cursor.sort(new BasicDBObject(queryOptions.sortAsc().get(0), -1));
            }

            if (queryOptions.limit() != null) {
                cursor.limit(queryOptions.limit());
            }
        }

        while (cursor.hasNext()) {
            Map<String, Object> map = new LinkedHashMap<>();

            DBObject doc = cursor.next();

            Set<String> docKeys = doc.keySet();

            for (String key : docKeys) {
                map.put(key, doc.get(key));
            }

            results.add(map);
        }

        return results;
    }

    public static void update(DB db, String collectionName, DBObject query, DBObject updateData) {
        db.getCollection(collectionName).update(query, updateData);
    }

    public static DB mongoSystemDB() {
        return (DB) new Connections().getSystemConnection();
    }

    public static DB mongoMerchantDB() {
        return (DB) new Connections().getFirstConnection("mongodb");
    }

    public static DB mongoDmaDB() {
        return (DB) new Connections().getConnection("mongodb.dma");
    }

    public static void removeFromArrayFiled(DB db, String collectionName, DBObject query, String field, Object value) {
        DBCollection col = db.getCollection(collectionName);
        DBObject fieldClause = new BasicDBObject();
        fieldClause.put(field, value);
        DBObject pullClause = new BasicDBObject();
        pullClause.put("$pull", fieldClause);
        col.update(query, pullClause, false, true);
    }
}
