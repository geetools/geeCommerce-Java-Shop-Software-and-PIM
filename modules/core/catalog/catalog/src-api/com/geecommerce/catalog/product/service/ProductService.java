package com.geecommerce.catalog.product.service;

import java.util.Collection;
import java.util.List;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.model.ProductLinkType;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.type.Id;

public interface ProductService extends Service {
    // ----------------------------------------------------------------------
    // Product
    // ----------------------------------------------------------------------

    public Product createProduct(Product product);

    public Product getProduct(Id id);

    public Product getProductById2(Id id2);

    public List<Product> getProducts(Collection<Id> productIds, QueryOptions queryOptions);

    public List<Product> getProducts(Id... ids);

    public List<Product> getProducts(boolean fetchEntities, Id... ids);

    public List<Product> getEnabledProducts();

    public List<Id> getEnabledProductIds();

    public List<Id> getAllProductIds();

    public List<Product> getProductsFor(Merchant merchant);

    public List<Product> getProductsFor(Store store);

    public void update(Product product);

    // ----------------------------------------------------------------------
    // Product Links
    // ----------------------------------------------------------------------

    public ProductLinkType createProductLinkType(ProductLinkType productLinkType);

    public void updateProductLinkType(ProductLinkType productLinkType);

    public ProductLinkType getProductLinkTypeFor(String code);

    public List<ProductLinkType> getProductLinkTypes();
}
