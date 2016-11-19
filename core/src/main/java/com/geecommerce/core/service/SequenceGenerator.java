package com.geecommerce.core.service;

import com.geecommerce.core.App;
import com.geecommerce.core.db.Connections;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class SequenceGenerator {
    public static Long nextSequenceNumber(String seqName) {
        if (seqName == null)
            throw new NullPointerException("A name must be provided when generating a sequence number.");

        Object connObj = App.get().inject(Connections.class).getDefaultMerchantConnection();

        if (connObj instanceof DB) {
            DB db = (DB) connObj;

            DBCollection col = db.getCollection("_sequence");

            DBObject query = new BasicDBObject();
            query.put("_id", seqName);

            DBObject change = new BasicDBObject("seq", 1);
            DBObject update = new BasicDBObject("$inc", change);

            DBObject res = col.findAndModify(query, new BasicDBObject(), new BasicDBObject(), false, update, true, true);

            Object seq = res.get("seq");

            if (seq != null) {
                if (seq instanceof Number) {
                    return ((Number) seq).longValue();
                } else if (seq instanceof String) {
                    return Long.parseLong((String) seq);
                }

                throw new IllegalStateException("Expecting a sequence of type Number but got " + seq.getClass().getName() + " instead");
            } else {
                throw new IllegalStateException("Expecting a sequence number but got NULL instead");
            }

        } else {
            // TODO
        }

        return null;
    }
}
