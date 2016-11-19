package com.geecommerce.catalog.product.repository;

import java.util.List;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.model.ProductList;
import com.geecommerce.catalog.product.model.ProductNavigationIndex;
import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.type.Id;

public interface ProductNavigationIndexes extends Repository {
    public List<ProductNavigationIndex> forProduct(Product product);

    public List<ProductNavigationIndex> forProduct(Product product, boolean visibleOnly);

    public List<ProductNavigationIndex> forProduct(Id productId);

    public List<ProductNavigationIndex> forProduct(Id productId, boolean visibleOnly);

    public List<ProductNavigationIndex> forProduct(Product product, Id navigationRootId);

    public List<ProductNavigationIndex> forProduct(Product product, Id navigationRootId, boolean visibleOnly);

    public List<ProductNavigationIndex> forProduct(Id productId, Id navigationRootId);

    public List<ProductNavigationIndex> forProduct(Id productId, Id navigationRootId, boolean visibleOnly);

    public List<ProductNavigationIndex> forValues(Product product, ProductList productList, Id navigationRootId);

    public List<ProductNavigationIndex> forValues(Product product, ProductList productList);
}
