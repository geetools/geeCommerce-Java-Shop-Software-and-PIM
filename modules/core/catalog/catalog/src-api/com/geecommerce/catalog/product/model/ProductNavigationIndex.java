package com.geecommerce.catalog.product.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public interface ProductNavigationIndex extends Model {
    public Id getId();

    public ProductNavigationIndex setId(Id id);

    public Id getProductListId();

    public ProductNavigationIndex setProductListId(Id productListId);

    public Id getProductId();

    public ProductNavigationIndex setProductId(Id productId);

    public Id getNavigationItemRootId();

    public ProductNavigationIndex setNavigationItemRootId(Id navigationItemRootId);

    public Id getNavigationItemId();

    public ProductNavigationIndex setNavigationItemId(Id navigationItemId);

    public int getNavigationLevel();

    public ProductNavigationIndex setNavigationLevel(int navigationLevel);

    public int getNavigationPosition();

    public ProductNavigationIndex setNavigationPosition(int navigationPosition);

    public int getParentNavigationLevel();

    public ProductNavigationIndex setParentNavigationLevel(int parentNavigationLevel);

    public int getParentNavigationPosition();

    public ProductNavigationIndex setParentNavigationPosition(int parentNavigationPosition);

    public boolean isVisible();

    public ProductNavigationIndex setVisible(boolean visible);

    public int getUpdateFlag();

    public ProductNavigationIndex setUpdateFlag(int updateFlag);

    public static class Col {
        public static final String ID = "_id";
        public static final String PRODUCT_LIST_ID = "prd_list_id";
        public static final String PRODUCT_ID = "prd_id";
        public static final String NAVIGATION_ITEM_ROOT_ID = "nav_item_root_id";
        public static final String NAVIGATION_ITEM_ID = "nav_item_id";
        public static final String NAVIGATION_LEVEL = "nav_item_level";
        public static final String NAVIGATION_POSITION = "nav_item_pos";
        public static final String PARENT_NAVIGATION_LEVEL = "par_nav_item_level";
        public static final String PARENT_NAVIGATION_POSITION = "par_nav_item_pos";
        public static final String VISIBLE = "visible";
        public static final String UPDATE_FLAG = "upd";
    }
}
