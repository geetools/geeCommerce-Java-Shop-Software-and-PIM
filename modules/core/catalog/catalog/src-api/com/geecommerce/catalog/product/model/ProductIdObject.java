package com.geecommerce.catalog.product.model;

import com.geecommerce.core.type.Id;

/**
 * This interface simply exposes the productId so that commons tasks on objects
 * that reference a product only have to be written once.
 * 
 * @author Michael Delamere
 */
public interface ProductIdObject {
    public Id getProductId();
}
