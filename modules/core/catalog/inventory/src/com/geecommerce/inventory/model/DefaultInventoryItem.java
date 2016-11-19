package com.geecommerce.inventory.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;

@XmlRootElement(name = "inventoryItem")
@XmlAccessorType(XmlAccessType.FIELD)
@Model(InventoryItem.TABLE_NAME)
public class DefaultInventoryItem extends AbstractModel implements InventoryItem {
    private static final long serialVersionUID = -4423678731596920606L;

    @Column(Col.ID)
    private Id id = null;

    @Column(Col.PRODUCT_ID)
    private Id productId = null;

    @Column(Col.STORE_ID)
    private Id storeId = null;

    @Column(Col.QTY)
    private Integer qty = null;

    @Column(Col.ALLOW_BACKORDER)
    private boolean allowBackorder = false;

    @Override
    public Id getId() {
	return id;
    }

    @Override
    public InventoryItem setId(Id id) {
	this.id = id;
	return this;
    }

    @Override
    public Id getProductId() {
	return productId;
    }

    @Override
    public InventoryItem setProductId(Id productId) {
	this.productId = productId;
	return this;
    }

    @Override
    public Id getStoreId() {
	return storeId;
    }

    @Override
    public InventoryItem setStoreId(Id storeId) {
	this.storeId = storeId;
	return this;
    }

    @Override
    public Integer getQty() {
	return qty;
    }

    @Override
    public InventoryItem setQty(Integer qty) {
	this.qty = qty;
	return this;
    }

    @Override
    public boolean isAllowBackorder() {
	return allowBackorder;
    }

    @Override
    public InventoryItem setAllowBackorder(boolean allowBackorder) {
	this.allowBackorder = allowBackorder;
	return this;
    }

    @Override
    public String toString() {
	return "DefaultInventoryItem [id=" + id + ", productId=" + productId + ", storeId=" + storeId + ", qty=" + qty + ", allowBackorder=" + allowBackorder + "]";
    }
}
