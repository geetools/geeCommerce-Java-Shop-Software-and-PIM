package com.geecommerce.retail.model;

import java.util.Map;

import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;
import com.geecommerce.retail.service.AvailabilityTextService;
import com.geecommerce.retail.service.RetailStoreService;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

@Model("retail_store_inventories")
public class DefaultRetailStoreInventory extends AbstractMultiContextModel implements RetailStoreInventory {
    private static final long serialVersionUID = -5576460787490243271L;
    private Id id = null;
    private Id retailStoreId = null;
    private Id productId = null;
    private Id availabilityTextId = null;
    private Integer quantity = null;
    private String availabilityText = null;
    private RetailStore retailStore = null;

    private final RetailStoreService retailStoreService;

    @Inject
    public DefaultRetailStoreInventory(RetailStoreService retailStoreService) {
        this.retailStoreService = retailStoreService;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public RetailStoreInventory setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public Id getRetailStoreId() {
        return retailStoreId;
    }

    @Override
    public RetailStore getRetailStore() {
        if (retailStore == null) {
            if (retailStoreId != null) {
                retailStore = retailStoreService.getRetailStore(retailStoreId);
            }
        }
        return retailStore;
    }

    @Override
    public RetailStoreInventory setRetailStoreId(Id retailStoreId) {
        this.retailStoreId = retailStoreId;
        return this;
    }

    @Override
    public Id getProductId() {
        return productId;
    }

    @Override
    public RetailStoreInventory setProductId(Id productId) {
        this.productId = productId;
        return this;
    }

    @Override
    public Id getAvailabilityTextId() {
        return availabilityTextId;
    }

    @Override
    public String getAvailabilityText() {
        if (availabilityText == null && availabilityTextId != null) {
            AvailabilityTextService ats = app.service(AvailabilityTextService.class);
            AvailabilityText text = ats.getAvailabilityText(getAvailabilityTextId());
            if (text != null) {
                availabilityText = text.getText();
            }
        }
        return availabilityText;
    }

    @Override
    public RetailStoreInventory setAvailabilityTextId(Id availabilityTextId) {
        this.availabilityTextId = availabilityTextId;
        return this;
    }

    @Override
    public Integer getQuantity() {
        return quantity;
    }

    @Override
    public RetailStoreInventory setQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null)
            return;

        super.fromMap(map);

        this.id = id_(map.get(Column.ID));
        this.retailStoreId = id_(map.get(Column.RETAIL_STORE_ID));
        this.productId = id_(map.get(Column.PRODUCT_ID));
        this.availabilityTextId = id_(map.get(Column.AVAILABILITY_TEXT_ID));
        this.quantity = int_(map.get(Column.QUANTITY));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

        map.put(Column.ID, getId());
        map.put(Column.RETAIL_STORE_ID, getRetailStoreId());
        map.put(Column.PRODUCT_ID, getProductId());
        map.put(Column.AVAILABILITY_TEXT_ID, getAvailabilityTextId());
        map.put(Column.QUANTITY, getQuantity());
        return map;
    }
}
