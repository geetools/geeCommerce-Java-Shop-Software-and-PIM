package com.geecommerce.catalog.product.repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.catalog.product.ProductStatus;
import com.geecommerce.catalog.product.dao.ProductDao;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.model.Product.Col;
import com.geecommerce.core.enums.ProductType;
import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.core.service.api.Dao;
import com.geecommerce.core.service.api.GlobalColumn;
import com.geecommerce.core.system.attribute.model.AttributeTargetObject;
import com.geecommerce.core.system.attribute.repository.AttributeTargetObjects;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;
import com.mongodb.QueryOperators;

@Repository
public class DefaultProducts extends AbstractRepository implements Products {
    protected final ProductDao productDao;
    protected final AttributeTargetObjects attributeTargetObjects;

    String ATTR_CODE_ARTICLE_NUMBER = "article_number";

    @Inject
    public DefaultProducts(ProductDao productDao, AttributeTargetObjects attributeTargetObjects) {
        this.productDao = productDao;
        this.attributeTargetObjects = attributeTargetObjects;
    }

    @Override
    public Dao dao() {
        return this.productDao;
    }

    @Override
    public List<Product> thatBelongTo(Merchant merchant) {
        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(GlobalColumn.MERCHANT_ID, merchant.getId());

        return find(Product.class, filter);
    }

    @Override
    public List<Product> thatBelongTo(Store store) {
        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(GlobalColumn.STORE_ID, store.getId());

        return find(Product.class, filter);
    }

    @Override
    public List<Product> enabledForContext() {
        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(Product.Col.STATUS, ProductStatus.ENABLED.toId());
        filter.put(Product.Col.DELETED, false);

        return simpleContextFind(Product.class, filter);
    }

    @Override
    public List<Id> enabledIdsForContext() {
        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(Product.Col.STATUS, ProductStatus.ENABLED.toId());
        filter.put(Product.Col.DELETED, false);

        return simpleContextFindIdsOnly(Product.class, filter);
    }

    @Override
    public List<Id> allIdsForContext() {
        return simpleContextFindIdsOnly(Product.class, new LinkedHashMap<>());
    }

    @Override
    public List<Id> noneDeletedIdsForContext() {
        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(Product.Col.DELETED, false);

        return simpleContextFindIdsOnly(Product.class, filter);
    }

    @Override
    public Product havingId2(Id id2) {
        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(Product.Col.ID2, id2);

        // TODO: what if the same id2 exists for different merchants?
        List<Product> products = find(Product.class, filter);

        return products == null || products.size() == 0 ? null : products.get(0);
    }

    @Override
    public Product havingArticleNumber(String articleNumber) {
        Map<String, Object> filter = new LinkedHashMap<>();

        AttributeTargetObject prdTargetObject = attributeTargetObjects.forType(Product.class);

        if (prdTargetObject == null)
            throw new IllegalStateException(
                "Unable to find an attribute target object entry in database for the model type '"
                    + Product.class.getName() + "'");

        appendAttributeCondition(Product.class, prdTargetObject, ATTR_CODE_ARTICLE_NUMBER, articleNumber, filter);

        List<Product> products = simpleContextFind(Product.class, filter);

        return products == null || products.size() == 0 ? null : products.get(0);
    }

    @Override
    public List<Product> havingProgrammeChildProduct(Product childProduct) {
        if (childProduct == null || childProduct.getId() == null)
            return null;

        Map<String, Object> in = new LinkedHashMap<>();
        in.put(QueryOperators.IN, new Id[] { childProduct.getId() });

        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(Col.PROGRAMME_PRODUCTS, in);
        filter.put(Product.Col.STATUS, ProductStatus.ENABLED.toId());
        filter.put(Product.Col.DELETED, false);
        filter.put(Product.Col.TYPE, ProductType.PROGRAMME.toId());

        return simpleContextFind(Product.class, filter);
    }

    @Override
    public Map<String, Id> allArticleNumbers() {
        return productDao.fetchAllArticleNumbers();
    }

    @Override
    public Map<Id, String> idArticleNumberMap() {
        return productDao.fetchIdArticleNumberMap();
    }
}
