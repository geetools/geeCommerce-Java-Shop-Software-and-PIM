package com.geecommerce.catalog.product.repository;

import java.util.List;
import java.util.Map;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.type.Id;

public interface Products extends Repository {
    public List<Product> thatBelongTo(Merchant merchant);

    public List<Product> thatBelongTo(Store store);

    public List<Product> enabledForContext();

    public Product havingId2(Id id2);

    public Product havingArticleNumber(String articleNumber);

    public List<Id> allIdsForContext();

    public List<Id> enabledIdsForContext();

    public List<Id> noneDeletedIdsForContext();

    public List<Product> havingProgrammeChildProduct(Product childProduct);

    public Map<String, Id> allArticleNumbers();

    public Map<Id, String> idArticleNumberMap();

    public void buildProductIdsCollection();

    public void buildTmpProductIdsCollection(String collectionName);

    public void dropTmpProductIdsCollection(String collectionName);

    public Product havingIds(String collectionName, Map<String, Object> havingIds);

    public Product havingIds(String collectionName, String id2, String articleNumber, Long ean);

    public Map<String, Object> productIds(String collectionName, Map<String, Object> havingIds);

    public Map<String, Object> productIds(String collectionName, String id2, String articleNumber, Long ean);

    public boolean contains(String collectionName, Id id);
}
