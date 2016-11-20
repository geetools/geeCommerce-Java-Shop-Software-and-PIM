package com.geecommerce.catalog.product.dao;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.cache.CacheManager;
import com.geecommerce.core.db.Connections;
import com.geecommerce.core.service.Annotations;
import com.geecommerce.core.service.annotation.Dao;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.service.persistence.mongodb.AbstractMongoDao;
import com.geecommerce.core.system.attribute.Attributes;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;

@Singleton
@Dao
public class DefaultProductDao extends AbstractMongoDao implements ProductDao {

    private static final String COLLECTION_NAME = "products";

    @Inject
    public DefaultProductDao(Connections connections, CacheManager cacheManager) {
        super(connections, cacheManager);
    }

    @Override
    protected <T extends Model> String getCollectionName(Class<T> modelClass) {
        return modelClass == null ? COLLECTION_NAME : Annotations.getCollectionName(modelClass);
    }

    @Override
    protected DB db(Class<? extends Model> modelClass) {
        return (DB) this.connections.getFirstConnection("mongodb");
    }

    @Override
    public Map<String, Id> fetchAllArticleNumbers() {
        Map<String, Id> articleNumbers = new LinkedHashMap<String, Id>();

        Id attributeId = Attributes.getAttributeId(Product.class, "article_number");

        // build the $projection operation
        DBObject fields = new BasicDBObject("attributes.attr_id", 1);
        fields.put("attributes.val.val", 1);
        DBObject project = new BasicDBObject("$project", fields);

        DBObject unwind = new BasicDBObject("$unwind", "$attributes");

        // create our pipeline operations, first with the $match
        DBObject match = new BasicDBObject("$match", new BasicDBObject("attributes.attr_id", attributeId));

        // build the $projection operation
        DBObject project2 = new BasicDBObject("$project", new BasicDBObject("an", "$attributes.val.val"));

        List<DBObject> pipeline = Arrays.asList(project, unwind, match, project2);

        AggregationOutput output = db(Product.class).getCollection(COLLECTION_NAME).aggregate(pipeline);

        for (DBObject result : output.results()) {
            if (result.get("an") != null) {
                articleNumbers.put(String.valueOf(((BasicDBList) result.get("an")).get(0)).trim(),
                    Id.valueOf(result.get("_id")));
            }
        }

        return articleNumbers;
    }

    @Override
    public Map<Id, String> fetchIdArticleNumberMap() {
        Map<Id, String> articleNumbers = new LinkedHashMap<Id, String>();

        Id attributeId = Attributes.getAttributeId(Product.class, "article_number");

        // build the $projection operation
        DBObject fields = new BasicDBObject("attributes.attr_id", 1);
        fields.put("attributes.val.val", 1);
        DBObject project = new BasicDBObject("$project", fields);

        DBObject unwind = new BasicDBObject("$unwind", "$attributes");

        // create our pipeline operations, first with the $match
        DBObject match = new BasicDBObject("$match", new BasicDBObject("attributes.attr_id", attributeId));

        // build the $projection operation
        DBObject project2 = new BasicDBObject("$project", new BasicDBObject("an", "$attributes.val.val"));

        List<DBObject> pipeline = Arrays.asList(project, unwind, match, project2);

        AggregationOutput output = db(Product.class).getCollection(COLLECTION_NAME).aggregate(pipeline);

        for (DBObject result : output.results()) {
            if (result.get("an") != null) {
                articleNumbers.put(Id.valueOf(result.get("_id")),
                    String.valueOf(((BasicDBList) result.get("an")).get(0)).trim());
            }
        }

        return articleNumbers;
    }
}
