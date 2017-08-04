package com.geecommerce.catalog.product.dao;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.Str;
import com.geecommerce.core.cache.CacheManager;
import com.geecommerce.core.db.Connections;
import com.geecommerce.core.service.Annotations;
import com.geecommerce.core.service.annotation.Dao;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.service.persistence.mongodb.AbstractMongoDao;
import com.geecommerce.core.system.attribute.Attributes;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.type.TypeConverter;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

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

    @SuppressWarnings("unchecked")
    @Override
    public void buildTmpProductIdsCollection(String collectionName) {
        if (COLLECTION_NAME.equals(collectionName) || Str.isEmpty(collectionName))
            throw new IllegalArgumentException("Unable to build collection '" + collectionName + "'");

        Id attributeId = Attributes.getAttributeId(Product.class, "article_number");

        StringBuilder query = new StringBuilder()
            .append("[")
            .append("{$project: {_id : 1, id2 : 1, ean : 1, 'attributes.attr_id' : 1, 'attributes.val.val' : 1}},")
            .append("{$unwind: '$attributes'},")
            .append("{$match: {'attributes.attr_id' : ").append(attributeId.str()).append("}},")
            .append("{$unwind: '$attributes.val'},")
            .append("{$project: {_id : 1, id2 : 1, ean : 1, article_number : '$attributes.val.val'}},")
            .append("{$out: '").append(collectionName).append("'}")
            .append(" ]");

        List<DBObject> pipeline = (List<DBObject>) JSON.parse(query.toString());

        db(Product.class).getCollection(COLLECTION_NAME).aggregate(pipeline);

        if (!db(Product.class).collectionExists(collectionName))
            throw new IllegalStateException("Unable to build collection '" + collectionName + "'");

        db(Product.class).getCollection(collectionName).createIndex((DBObject) JSON.parse("{ _id : 1, id2 : 1, ean : 1, article_number : 1 }"));
        db(Product.class).getCollection(collectionName).createIndex((DBObject) JSON.parse("{ id2 : 1 }"));
        db(Product.class).getCollection(collectionName).createIndex((DBObject) JSON.parse("{ ean : 1 }"));
        db(Product.class).getCollection(collectionName).createIndex((DBObject) JSON.parse("{ article_number : 1 }"));
    }

    @Override
    public void dropTmpProductIdsCollection(String collectionName) {
        if (COLLECTION_NAME.equals(collectionName) || Str.isEmpty(collectionName))
            throw new IllegalArgumentException("Unable to drop collection '" + collectionName + "'");

        if (!db(Product.class).collectionExists(collectionName))
            return;

        db(Product.class).getCollection(collectionName).drop();
    }

    @Override
    public Product findProductByIds(String collectionName, String id2, String articleNumber, Long ean) {
        Map<String, Object> productIds = fetchProductIds(collectionName, id2, articleNumber, ean);

        if (productIds != null && productIds.containsKey("_id")) {
            Product p = findById(Product.class, Id.valueOf(productIds.get("_id")));
            return p;
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> fetchProductIds(String collectionName, String id2, String articleNumber, Long ean) {
        if (!db(Product.class).collectionExists(collectionName))
            throw new IllegalStateException("Collection '" + collectionName + "' does not exist");

        int countNotNull = 0;

        if (!Str.isEmpty(id2))
            countNotNull++;

        if (!Str.isEmpty(articleNumber))
            countNotNull++;

        if (ean != null)
            countNotNull++;

        if (countNotNull == 0)
            return null;

        boolean isOrQuery = countNotNull > 1;

        DBObject dbObject = null;

        if (isOrQuery) {
            StringBuilder query = new StringBuilder()
                .append("{$or: [");

            if (id2 != null)
                query.append("{id2: '").append(id2).append("'},");

            if (articleNumber != null)
                query.append("{article_number: '").append(articleNumber).append("'}");

            if (ean != null)
                query.append(", {ean: ").append(ean).append("}");

            query.append("]}");

            dbObject = (DBObject) JSON.parse(query.toString());
        } else {
            if (id2 != null)
                dbObject = new BasicDBObject("id2", id2);

            if (articleNumber != null)
                dbObject = new BasicDBObject("article_number", articleNumber);

            if (ean != null)
                dbObject = new BasicDBObject("ean", ean);
        }

        DBCursor cursor = null;
        DBObject doc = null;

        try {
            cursor = db(Product.class).getCollection(collectionName).find(dbObject);

            int count = 0;

            while (cursor.hasNext()) {
                if (count > 0)
                    throw new IllegalStateException("Unable to find unique product for the ids [id2=" + id2 + ", articleNumber=" + articleNumber + ", ean=" + ean + "]");

                doc = cursor.next();

                if (doc != null) {
                    if (doc.get("_id") != null)
                        doc.put("_id", TypeConverter.asId(doc.get("_id")));

                    if (doc.get("ean") != null)
                        doc.put("ean", TypeConverter.asLong(doc.get("ean")));
                }

                count++;
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return doc == null ? null : doc.toMap();
    }

    @Override
    public boolean productIdExists(String collectionName, Id id) {
        if (!db(Product.class).collectionExists(collectionName))
            throw new IllegalStateException("Collection '" + collectionName + "' does not exist");

        DBObject dbObject = new BasicDBObject("_id", id);

        DBObject doc = db(Product.class).getCollection(collectionName).findOne(dbObject);

        return doc != null;
    }
}
