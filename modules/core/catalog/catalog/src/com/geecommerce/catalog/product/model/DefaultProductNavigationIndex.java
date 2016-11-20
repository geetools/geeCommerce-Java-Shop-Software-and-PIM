package com.geecommerce.catalog.product.model;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;

@Cacheable
@Model(collection = "idx_product_navigation", context = "store", fieldAccess = true)
public class DefaultProductNavigationIndex extends AbstractModel implements ProductNavigationIndex {
    private static final long serialVersionUID = 7441763395392128333L;

    @Column(Col.ID)
    private Id id = null;

    @Column(Col.PRODUCT_LIST_ID)
    private Id productListId = null;

    @Column(Col.PRODUCT_ID)
    private Id productId = null;

    @Column(Col.NAVIGATION_ITEM_ROOT_ID)
    private Id navigationItemRootId = null;

    @Column(Col.NAVIGATION_ITEM_ID)
    private Id navigationItemId = null;

    @Column(Col.NAVIGATION_LEVEL)
    private int navigationLevel = 0;

    @Column(Col.NAVIGATION_POSITION)
    private int navigationPosition = 0;

    @Column(Col.PARENT_NAVIGATION_LEVEL)
    private int parentNavigationLevel = 0;

    @Column(Col.PARENT_NAVIGATION_POSITION)
    private int parentNavigationPosition = 0;

    @Column(Col.VISIBLE)
    private Boolean visible = false;

    @Column(Col.UPDATE_FLAG)
    private int updateFlag = 0;

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public ProductNavigationIndex setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public Id getProductListId() {
        return productListId;
    }

    @Override
    public ProductNavigationIndex setProductListId(Id productListId) {
        this.productListId = productListId;
        return this;
    }

    @Override
    public Id getProductId() {
        return productId;
    }

    @Override
    public ProductNavigationIndex setProductId(Id productId) {
        this.productId = productId;
        return this;
    }

    @Override
    public Id getNavigationItemRootId() {
        return navigationItemRootId;
    }

    @Override
    public ProductNavigationIndex setNavigationItemRootId(Id navigationItemRootId) {
        this.navigationItemRootId = navigationItemRootId;
        return this;
    }

    @Override
    public Id getNavigationItemId() {
        return navigationItemId;
    }

    @Override
    public ProductNavigationIndex setNavigationItemId(Id navigationItemId) {
        this.navigationItemId = navigationItemId;
        return this;
    }

    @Override
    public int getNavigationLevel() {
        return navigationLevel;
    }

    @Override
    public ProductNavigationIndex setNavigationLevel(int navigationLevel) {
        this.navigationLevel = navigationLevel;
        return this;
    }

    @Override
    public int getNavigationPosition() {
        return navigationPosition;
    }

    @Override
    public ProductNavigationIndex setNavigationPosition(int navigationPosition) {
        this.navigationPosition = navigationPosition;
        return this;
    }

    @Override
    public int getParentNavigationLevel() {
        return parentNavigationLevel;
    }

    @Override
    public ProductNavigationIndex setParentNavigationLevel(int parentNavigationLevel) {
        this.parentNavigationLevel = parentNavigationLevel;
        return this;
    }

    @Override
    public int getParentNavigationPosition() {
        return parentNavigationPosition;
    }

    @Override
    public ProductNavigationIndex setParentNavigationPosition(int parentNavigationPosition) {
        this.parentNavigationPosition = parentNavigationPosition;
        return this;
    }

    @Override
    public boolean isVisible() {
        return visible == null ? false : true;
    }

    @Override
    public ProductNavigationIndex setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    @Override
    public int getUpdateFlag() {
        return updateFlag;
    }

    @Override
    public ProductNavigationIndex setUpdateFlag(int updateFlag) {
        this.updateFlag = updateFlag;
        return this;
    }
}
