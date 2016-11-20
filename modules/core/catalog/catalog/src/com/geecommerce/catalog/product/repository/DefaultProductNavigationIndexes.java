package com.geecommerce.catalog.product.repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.model.ProductList;
import com.geecommerce.catalog.product.model.ProductNavigationIndex;
import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.core.type.Id;

@Repository
public class DefaultProductNavigationIndexes extends AbstractRepository implements ProductNavigationIndexes {
    @Override
    public List<ProductNavigationIndex> forProduct(Product product) {
        return forProduct(product, true);
    }

    @Override
    public List<ProductNavigationIndex> forProduct(Product product, boolean visibleOnly) {
        if (product == null || product.getId() == null)
            return null;

        return forProduct(product.getId(), visibleOnly);
    }

    @Override
    public List<ProductNavigationIndex> forProduct(Id productId) {
        return forProduct(productId, true);
    }

    @Override
    public List<ProductNavigationIndex> forProduct(Id productId, boolean visibleOnly) {
        if (productId == null)
            return null;

        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(ProductNavigationIndex.Col.PRODUCT_ID, productId);

        if (visibleOnly)
            filter.put(ProductNavigationIndex.Col.VISIBLE, true);

        return find(ProductNavigationIndex.class, filter,
            QueryOptions.builder().sortByDesc(ProductNavigationIndex.Col.NAVIGATION_LEVEL)
                .sortBy(ProductNavigationIndex.Col.NAVIGATION_POSITION).build());
    }

    @Override
    public List<ProductNavigationIndex> forProduct(Product product, Id navigationRootId) {
        return forProduct(product, navigationRootId, true);
    }

    @Override
    public List<ProductNavigationIndex> forProduct(Product product, Id navigationRootId, boolean visibleOnly) {
        if (product == null || product.getId() == null || navigationRootId == null)
            return null;

        return forProduct(product.getId(), navigationRootId, visibleOnly);
    }

    @Override
    public List<ProductNavigationIndex> forProduct(Id productId, Id navigationRootId) {
        return forProduct(productId, navigationRootId, true);
    }

    @Override
    public List<ProductNavigationIndex> forProduct(Id productId, Id navigationRootId, boolean visibleOnly) {
        if (productId == null || navigationRootId == null)
            return null;

        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(ProductNavigationIndex.Col.PRODUCT_ID, productId);
        filter.put(ProductNavigationIndex.Col.NAVIGATION_ITEM_ROOT_ID, navigationRootId);

        if (visibleOnly)
            filter.put(ProductNavigationIndex.Col.VISIBLE, true);

        return find(ProductNavigationIndex.class, filter,
            QueryOptions.builder().sortByDesc(ProductNavigationIndex.Col.NAVIGATION_LEVEL)
                .sortBy(ProductNavigationIndex.Col.NAVIGATION_POSITION)
                .sortBy(ProductNavigationIndex.Col.PARENT_NAVIGATION_POSITION).build());
    }

    @Override
    public List<ProductNavigationIndex> forValues(Product product, ProductList productList, Id navigationRootId) {
        if (product == null || product.getId() == null)
            return null;

        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(ProductNavigationIndex.Col.PRODUCT_ID, product.getId());
        filter.put(ProductNavigationIndex.Col.PRODUCT_LIST_ID, productList.getId());
        filter.put(ProductNavigationIndex.Col.NAVIGATION_ITEM_ROOT_ID, navigationRootId);
        filter.put(ProductNavigationIndex.Col.VISIBLE, true);

        return find(ProductNavigationIndex.class, filter,
            QueryOptions.builder().sortByDesc(ProductNavigationIndex.Col.NAVIGATION_LEVEL)
                .sortBy(ProductNavigationIndex.Col.NAVIGATION_POSITION).build());
    }

    @Override
    public List<ProductNavigationIndex> forValues(Product product, ProductList productList) {
        if (product == null || product.getId() == null)
            return null;

        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(ProductNavigationIndex.Col.PRODUCT_ID, product.getId());
        filter.put(ProductNavigationIndex.Col.PRODUCT_LIST_ID, productList.getId());
        filter.put(ProductNavigationIndex.Col.VISIBLE, true);

        return find(ProductNavigationIndex.class, filter,
            QueryOptions.builder().sortByDesc(ProductNavigationIndex.Col.NAVIGATION_LEVEL)
                .sortBy(ProductNavigationIndex.Col.NAVIGATION_POSITION).build());
    }
}
