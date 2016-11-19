package com.geecommerce.catalog.product.repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.model.ProductConnectionIndex;
import com.geecommerce.core.reflect.Reflect;
import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.Annotations;
import com.geecommerce.core.service.ColumnInfo;
import com.geecommerce.core.service.Models;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.type.Ids;

@Repository
public class DefaultProductConnectionIndexes extends AbstractRepository implements ProductConnectionIndexes {
    @Override
    public ProductConnectionIndex forProduct(Product product) {
        if (product == null || product.getId() == null)
            return null;

        return forProduct(product.getId());
    }

    @Override
    public ProductConnectionIndex forProduct(Id productId) {
        if (productId == null)
            return null;

        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(ProductConnectionIndex.Col.PRODUCT_ID, productId);

        return findOne(ProductConnectionIndex.class, filter);
    }

    @Override
    public Map<Id, ProductConnectionIndex> forProducts(List<Product> products) {
        if (products == null || products.isEmpty())
            return null;

        return forProducts(Ids.toIds(products));
    }

    @Override
    public Map<Id, ProductConnectionIndex> forProducts(Id... productIds) {
        if (productIds == null || productIds.length == 0)
            return null;

        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(ProductConnectionIndex.Col.PRODUCT_ID, productIds);

        List<ProductConnectionIndex> productConnections = find(ProductConnectionIndex.class, filter);

        return toKeyObjectMap(productConnections, ProductConnectionIndex.Col.PRODUCT_ID);
    }

    @SuppressWarnings("unchecked")
    protected <K extends Object, V extends Model> Map<K, V> toKeyObjectMap(List<V> objects, String keyField) {
        if (objects == null || objects.size() == 0)
            return null;

        Map<K, V> KeyObjectMap = new LinkedHashMap<>();

        V object = objects.get(0);
        Class<V> clazz = (Class<V>) object.getClass();

        List<ColumnInfo> columnInfos = Annotations.getColumns(clazz);

        String fieldName = Models.fieldName(columnInfos, keyField);

        for (V obj : objects) {
            KeyObjectMap.put((K) Reflect.invokeGetter(clazz, obj, fieldName), obj);
        }

        return KeyObjectMap;
    }

}
