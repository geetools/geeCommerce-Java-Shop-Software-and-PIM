package com.geecommerce.catalog.product.observer;

import com.geecommerce.core.elasticsearch.helper.ElasticsearchIndexHelper;
import com.google.inject.Inject;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.repository.Products;
import com.geecommerce.core.event.Event;
import com.geecommerce.core.event.Observable;
import com.geecommerce.core.event.Observer;
import com.geecommerce.core.event.Run;
import com.geecommerce.core.event.annotation.Observe;
import com.geecommerce.core.type.Id;

@Observe(name = "catalog.product.model.Product", event = { Event.AFTER_NEW, Event.AFTER_UPDATE }, run = Run.ASYNCHRONOUSLY)
public class ProductObserver implements Observer {
    private final Products products;
    private final ElasticsearchIndexHelper elasticsearchHelper;

    @Inject
    public ProductObserver(Products products, ElasticsearchIndexHelper elasticsearchHelper) {
	this.products = products;
	this.elasticsearchHelper = elasticsearchHelper;
    }

    @Override
    public void onEvent(Event evt, Observable o) {
	if (o == null)
	    return;

	Id productId = ((Product) o).getId();

	System.out.println("[" + Thread.currentThread().getName() + "] Running asynchronous observer for: " + productId + " -> " + evt.name());

	Product p = products.findById(Product.class, productId);

	elasticsearchHelper.updateIndexedItem(p, true);

	if (p.isVariant()) {
	    Product parent = p.getParent();

	    if (parent != null) {
		System.out.println("[" + Thread.currentThread().getName() + "] Running asynchronous update for parent: " + parent.getId());

		elasticsearchHelper.updateIndexedItem(parent, true);
	    }
	}
    }
}
