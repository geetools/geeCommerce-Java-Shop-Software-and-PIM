package com.geecommerce.catalog.product.dao;

import java.util.Map;

import com.geecommerce.core.service.persistence.mongodb.MongoDao;
import com.geecommerce.core.type.Id;

public interface ProductDao extends MongoDao {

    public Map<String, Id> fetchAllArticleNumbers();

    public Map<Id, String> fetchIdArticleNumberMap();
}
