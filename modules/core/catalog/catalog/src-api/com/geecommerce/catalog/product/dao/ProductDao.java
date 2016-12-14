package com.geecommerce.catalog.product.dao;

import java.util.Map;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.service.persistence.mongodb.MongoDao;
import com.geecommerce.core.type.Id;

public interface ProductDao extends MongoDao {

    public Map<String, Id> fetchAllArticleNumbers();

    public Map<Id, String> fetchIdArticleNumberMap();

    public void buildTmpProductIdsCollection(String collectionName);

    public void dropTmpProductIdsCollection(String collectionName);

    public Product findProductByIds(String collectionName, String id2, String articleNumber, Long ean);

    public Map<String, Object> fetchProductIds(String collectionName, String id2, String articleNumber, Long ean);

    public boolean productIdExists(String collectionName, Id id);
}
