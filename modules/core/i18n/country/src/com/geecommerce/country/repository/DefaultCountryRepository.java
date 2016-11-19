package com.geecommerce.country.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.core.service.api.Dao;
import com.geecommerce.core.service.persistence.mongodb.MongoDao;
import com.geecommerce.core.system.model.Country;
import com.google.inject.Inject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 */
@Repository
public class DefaultCountryRepository extends AbstractRepository implements CountryRepository {

    private MongoDao mongoDao;

    @Inject
    public DefaultCountryRepository(MongoDao mongoDao) {
        this.mongoDao = mongoDao;
    }

    @Override
    public List<Country> getAll() {
        return mongoDao.findAll(Country.class);
    }

    @Override
    public Country getByCode(String code) {
        Map<String, Object> filter = new HashMap<String, Object>(1);
        filter.put(Country.Col.CODE, code);
        return mongoDao.findOne(Country.class, filter);
    }

    @Override
    public Country getByPhoneCode(String code) {
        Map<String, Object> filter = new HashMap<String, Object>(1);
        filter.put(Country.Col.PHONE_CODE, code);
        return mongoDao.findOne(Country.class, filter);
    }

    @Override
    public List<Country> getWithNotEmptyField(String... fields) {
        Map<String, Object> filter = new HashMap<String, Object>(fields.length);
        for (String field : fields) {
            DBObject neClause = new BasicDBObject();
            neClause.put("$ne", null);
            filter.put(field, neClause);
        }
        return mongoDao.find(Country.class, filter);
    }

    @Override
    public Dao dao() {
        return mongoDao;
    }
}
