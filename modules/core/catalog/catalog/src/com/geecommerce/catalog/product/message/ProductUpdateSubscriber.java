package com.geecommerce.catalog.product.message;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.repository.Products;
import com.geecommerce.core.elasticsearch.helper.ElasticsearchIndexHelper;
import com.geecommerce.core.event.Run;
import com.geecommerce.core.message.Context;
import com.geecommerce.core.message.Subscriber;
import com.geecommerce.core.message.annotation.Subscribe;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;

@Subscribe(message = "product:update", run = Run.ASYNCHRONOUSLY)
public class ProductUpdateSubscriber implements Subscriber {
    private final Products products;
    private final ElasticsearchIndexHelper elasticsearchHelper;

    @Inject
    public ProductUpdateSubscriber(Products products, ElasticsearchIndexHelper elasticsearchHelper) {
        this.products = products;
        this.elasticsearchHelper = elasticsearchHelper;
    }

    @Override
    public void onMessage(Context ctx) {
        Id productId = ctx.get("productId");

        Product p = products.findById(Product.class, productId);

        System.out.println("Running asynchronous update for: " + productId);

        elasticsearchHelper.updateIndexedItem(p, true);

        if (p.isVariant()) {
            Product parent = p.getParent();

            if (parent != null) {
                System.out.println("Running asynchronous update for parent: " + parent.getId());

                elasticsearchHelper.updateIndexedItem(parent, true);
            }
        }
    }
}
