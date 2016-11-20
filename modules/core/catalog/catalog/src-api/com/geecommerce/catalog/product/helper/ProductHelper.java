package com.geecommerce.catalog.product.helper;

import java.util.List;
import java.util.Map;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.model.ProductIdObject;
import com.geecommerce.core.service.api.Helper;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeInputCondition;
import com.geecommerce.core.system.attribute.model.AttributeOption;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.type.Id;

public interface ProductHelper extends Helper {
    public Map<String, Object> toVariantsMap(Product product);

    public void completeProducts(List<Product> products, List<UrlRewrite> urlRewrites);

    public <T extends ProductIdObject> Id[] filterCompletedProductIds(List<T> productReferenceObjects,
        Id[] allProductIds);

    public <T extends ProductIdObject> Map<Id, List<T>> toProductIdListMap(List<T> objectsWithProductId);

    public String getAttributeOrConfigProperty(Product product, String attrName, String configPropertyName);

    public void rememberCurrentProductList(String currentListName, List<Id> productIds);

    public List<Id> getCurrentProductList(String currentListName);

    public Id getPreviousProductId(String currentListName, Id currentProductId);

    public Id getNextProductId(String currentListName, Id currentProductId);

    public AttributeOption getDescriptionStatus(Product product, Store store);

    public AttributeOption getImageStatus(Product product);

    public boolean isAttributeAvailableForProduct(Attribute attr, List<AttributeInputCondition> inputConditions,
        Product product);
}
