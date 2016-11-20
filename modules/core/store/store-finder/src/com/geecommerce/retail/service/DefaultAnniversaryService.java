package com.geecommerce.retail.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.service.persistence.mongodb.MongoDao;
import com.geecommerce.retail.model.Anniversary;
import com.google.inject.Inject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 */
@Service
public class DefaultAnniversaryService implements AnniversaryService {

    private final MongoDao mongoDao;

    @Inject
    public DefaultAnniversaryService(MongoDao mongoDao) {
        this.mongoDao = mongoDao;
    }

    @Override
    public List<Anniversary> findAllEndDateAfter(Date date) {
        Map<String, Object> filter = new HashMap<>();

        DBObject gteClause = new BasicDBObject();
        gteClause.put("$gte", date);
        filter.put(Anniversary.Column.END_DATE, gteClause);

        return mongoDao.find(Anniversary.class, filter,
            (new QueryOptions.Builder()).sortBy("startDate").sortBy("branchName").build());
    }
}
