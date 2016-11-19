package com.geecommerce.core.type;

import java.util.ArrayList;
import java.util.List;

public class ProductIds {
    /**
     * Converts arbitrary ProductIdObjects to Id list.
     * 
     * @param productIdObjects
     * @return ids
     */
    public static final <T extends ProductIdSupport> List<Id> toIdList(List<T> productIdObjects) {
	List<Id> ids = new ArrayList<>();

	if (productIdObjects == null || productIdObjects.size() == 0)
	    return ids;

	for (ProductIdSupport productIdObject : productIdObjects) {
	    ids.add(productIdObject.getProductId());
	}

	return ids;
    }
}
