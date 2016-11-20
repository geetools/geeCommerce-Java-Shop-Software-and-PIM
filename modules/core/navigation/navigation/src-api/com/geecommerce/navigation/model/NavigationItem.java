package com.geecommerce.navigation.model;

import java.util.List;

import com.geecommerce.core.enums.ObjectType;
import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.owlike.genson.annotation.JsonIgnore;

public interface NavigationItem extends MultiContextModel {
    public Id getId();

    @JsonIgnore
    public Id getIdStr();

    public NavigationItem setId(Id id);

    public Id getParentId();

    public NavigationItem setParentId(Id parentId);

    public Id getRootId();

    public NavigationItem setRootId(Id rootId);

    public Id getId2();

    public NavigationItem setId2(Id id2);

    public String getKey();

    public NavigationItem setKey(String key);

    public ContextObject<String> getLabel();

    public NavigationItem setLabel(ContextObject<String> label);

    public int getPosition();

    public NavigationItem setPosition(int position);

    public int getLevel();

    public NavigationItem setLevel(int level);

    public Id getTargetObjectId();

    public NavigationItem setTargetObjectId(Id targetObjectId);

    public ObjectType getTargetObjectType();

    public NavigationItem setTargetObjectType(ObjectType targetObjectType);

    public boolean isUseTargetObjectLabel();

    public NavigationItem setUseTargetObjectLabel(boolean useTargetObjectLabel);

    public ContextObject<String> getExternalURL();

    public NavigationItem setExternalURL(ContextObject<String> externalUrl);

    public boolean isEnabled();

    public NavigationItem setEnabled(boolean enabled);

    public String getDisplayLabel();

    @JsonIgnore
    public String getDisplayURI();

    @JsonIgnore
    public boolean isForProductList();

    @JsonIgnore
    public boolean isForProduct();

    @JsonIgnore
    public boolean isForCMS();

    @JsonIgnore
    public boolean hasExternalURL();

    public void loadTree();

    public NavigationItem setParent(NavigationItem parent);

    @JsonIgnore
    public NavigationItem getParent();

    @JsonIgnore
    public boolean hasParent();

    @JsonIgnore
    public List<NavigationItem> getChildren();

    @JsonIgnore
    public NavigationItem setChildren(List<NavigationItem> navigationItems);

    @JsonIgnore
    public boolean hasChildren();

    public NavigationItem traverseUpTo(int level);

    public void collectIds(List<Id> targetList);

    public void flatten(List<NavigationItem> targetList);

    static final class Col {
        public static final String ID = "_id";
        public static final String PARENT_ID = "parent_id";
        public static final String ROOT_ID = "root_id";
        public static final String ID2 = "id2";

        public static final String KEY = "key";
        public static final String LABEL = "label";
        public static final String POSITION = "position";
        public static final String LEVEL = "level";
        public static final String TARGET_OBJECT_ID = "tar_obj";
        public static final String TARGET_OBJECT_TYPE = "tar_obj_type";
        public static final String TARGET_OBJECT_LABEL = "tar_obj_label";
        public static final String EXTERNAL_URL = "ext_url";
        public static final String ENABLED = "enabled";
    }
}
