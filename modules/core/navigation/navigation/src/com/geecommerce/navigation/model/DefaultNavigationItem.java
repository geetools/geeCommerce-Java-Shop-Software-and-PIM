package com.geecommerce.navigation.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.model.ProductList;
import com.geecommerce.catalog.product.repository.ProductLists;
import com.geecommerce.catalog.product.repository.Products;
import com.geecommerce.core.enums.ObjectType;
import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.TargetSupport;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.system.repository.UrlRewrites;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.ContextObjects;
import com.geecommerce.core.type.Id;
import com.geecommerce.guiwidgets.model.Content;
import com.geecommerce.guiwidgets.repository.Contents;
import com.geecommerce.navigation.repository.NavigationItems;
import com.google.inject.Inject;
import com.owlike.genson.annotation.JsonIgnore;

@Cacheable
@Model("navigation_items")
public class DefaultNavigationItem extends AbstractMultiContextModel implements NavigationItem {
    private static final long serialVersionUID = -6927276841061370646L;
    @Column(Col.ID)
    private Id id = null;
    @Column(Col.PARENT_ID)
    private Id parentId = null;
    @Column(Col.ROOT_ID)
    private Id rootId = null;
    @Column(Col.ID2)
    private Id id2 = null;

    @Column(Col.KEY)
    private String key = null;
    @Column(Col.LABEL)
    private ContextObject<String> label = null;
    @Column(Col.LEVEL)
    private int level = 0;
    @Column(Col.POSITION)
    private int position = 0;

    @Column(Col.TARGET_OBJECT_ID)
    private Id targetObjectId = null;
    @Column(Col.TARGET_OBJECT_TYPE)
    private ObjectType targetObjectType = null;
    @Column(Col.TARGET_OBJECT_LABEL)
    private boolean useTargetObjectLabel = false;

    @Column(Col.EXTERNAL_URL)
    private ContextObject<String> externalURL = null;

    @Column(Col.ENABLED)
    private boolean enabled = false;

    // Loaded lazily
    private String displayURI = null;
    private TargetSupport targetObject = null;

    @JsonIgnore
    private NavigationItem parent = null;
    @JsonIgnore
    private List<NavigationItem> children = null;

    // Repositories
    private final NavigationItems navigationItems;
    private final UrlRewrites urlRewrites;
    private final ProductLists productLists;
    private final Products products;
    private final Contents contents;

    @Inject
    public DefaultNavigationItem(NavigationItems navigationItems, UrlRewrites urlRewrites, ProductLists productLists,
        Products products, Contents contents) {
        this.navigationItems = navigationItems;
        this.urlRewrites = urlRewrites;
        this.productLists = productLists;
        this.products = products;
        this.contents = contents;
    }

    @Override
    public Id getId() {
        return id;
    }

    @JsonIgnore
    @Override
    public Id getIdStr() {
        return id;
    }

    @Override
    public NavigationItem setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public Id getParentId() {
        if (parentId == null && parent != null) {
            parentId = parent.getId();
        }

        return parentId;
    }

    @Override
    public NavigationItem setParentId(Id parentId) {
        this.parentId = parentId;
        this.parent = null;
        return this;
    }

    @Override
    public Id getRootId() {
        return rootId;
    }

    @Override
    public NavigationItem setRootId(Id rootId) {
        this.rootId = rootId;
        return this;
    }

    @Override
    public Id getId2() {
        return id2;
    }

    @Override
    public NavigationItem setId2(Id id2) {
        this.id2 = id2;
        return this;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public NavigationItem setKey(String key) {
        this.key = key;
        return this;
    }

    @Override
    public ContextObject<String> getLabel() {
        return label;
    }

    @Override
    public NavigationItem setLabel(ContextObject<String> label) {
        this.label = label;
        return this;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public NavigationItem setPosition(int position) {
        this.position = position;
        return this;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public NavigationItem setLevel(int level) {
        this.level = level;
        return this;
    }

    @Override
    public Id getTargetObjectId() {
        return targetObjectId;
    }

    @Override
    public NavigationItem setTargetObjectId(Id targetObjectId) {
        this.targetObjectId = targetObjectId;
        return this;
    }

    @Override
    public ObjectType getTargetObjectType() {
        return targetObjectType;
    }

    @Override
    public NavigationItem setTargetObjectType(ObjectType targetObjectType) {
        this.targetObjectType = targetObjectType;
        return this;
    }

    @Override
    public boolean isUseTargetObjectLabel() {
        return useTargetObjectLabel;
    }

    @Override
    public NavigationItem setUseTargetObjectLabel(boolean useTargetObjectLabel) {
        this.useTargetObjectLabel = useTargetObjectLabel;
        return this;
    }

    @Override
    public ContextObject<String> getExternalURL() {
        return externalURL;
    }

    @Override
    public NavigationItem setExternalURL(ContextObject<String> externalURL) {
        this.externalURL = externalURL;
        return this;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public NavigationItem setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    protected TargetSupport getTargetObject() {
        if (targetObject == null && targetObjectType != null && targetObjectId != null) {
            Object obj = null;

            switch (targetObjectType) {
            case PRODUCT_LIST:
                obj = (ProductList) productLists.findById(ProductList.class, targetObjectId);
                break;
            case PRODUCT:
                obj = (Product) products.findById(Product.class, targetObjectId);
                break;
            case CMS:
                obj = (Content) contents.findById(Content.class, targetObjectId);
                break;
            default:
                throw new RuntimeException("TargetObjectType '" + targetObjectType.name() + "' not supported yet.");
            }

            targetObject = (TargetSupport) obj;
        }

        return targetObject;
    }

    @Override
    public String getDisplayLabel() {
        if (useTargetObjectLabel && !ObjectType.LINK.equals(targetObjectType)) {
            TargetSupport tarObject = getTargetObject();
            return tarObject != null ? (tarObject.getLabel() != null ? tarObject.getLabel().getVal() : "???") : "???";
        } else {
            return getLabel() != null ? getLabel().getVal() : "???";
        }

    }

    @JsonIgnore
    @Override
    public String getDisplayURI() {
        if (displayURI == null) {
            if (hasExternalURL()) {
                displayURI = externalURL.getStr();
            } else {
                UrlRewrite urlRewrite = urlRewrites.forTargetObject(targetObjectId, targetObjectType);

                if (urlRewrite != null)
                    displayURI = ContextObjects.findCurrentLanguageOrGlobal(urlRewrite.getRequestURI());
                else {
                    TargetSupport targetSupport = getTargetObject();

                    if (targetSupport != null)
                        displayURI = targetSupport.getURI().getStr();
                }
            }
        }

        return displayURI == null ? "???" : displayURI;
    }

    @JsonIgnore
    @Override
    public boolean isForProductList() {
        return targetObjectType != null && targetObjectType.equals(ObjectType.PRODUCT_LIST);
    }

    @JsonIgnore
    @Override
    public boolean isForProduct() {
        return targetObjectType != null && targetObjectType.equals(ObjectType.PRODUCT);
    }

    @JsonIgnore
    @Override
    public boolean isForCMS() {
        return targetObjectType != null && targetObjectType.equals(ObjectType.CMS);
    }

    @JsonIgnore
    @Override
    public boolean hasExternalURL() {
        return !isForProductList() && !isForProduct() && !isForCMS() && externalURL != null
            && externalURL.getStr() != null;
    }

    @Override
    public void loadTree() {
        if (children == null) {
            children = navigationItems.havingParent(this);

            for (NavigationItem child : children) {
                child.loadTree();
            }
        }
    }

    @Override
    public NavigationItem setParent(NavigationItem parent) {
        if (parent == null)
            this.parentId = null;
        else
            this.parentId = parent.getId();
        this.parent = parent;
        return this;
    }

    @JsonIgnore
    @Override
    public NavigationItem getParent() {
        if (parentId != null && parent == null) {
            parent = navigationItems.findById(NavigationItem.class, parentId);
        }

        return parent;
    }

    @JsonIgnore
    @Override
    public boolean hasParent() {
        NavigationItem parent = getParent();

        return parent != null;
    }

    @JsonIgnore
    @Override
    public List<NavigationItem> getChildren() {
        if (children == null) {
            children = navigationItems.havingParent(this);
        }

        return children;
    }

    @Override
    public NavigationItem setChildren(List<NavigationItem> navigationItems) {
        this.children = navigationItems;
        return this;
    }

    @JsonIgnore
    @Override
    public boolean hasChildren() {
        List<NavigationItem> children = getChildren();

        return children != null && children.size() > 0;
    }

    @Override
    public NavigationItem traverseUpTo(int level) {
        if (this.level == level) {
            return this;
        } else {
            NavigationItem p = getParent();

            if (p != null) {
                return p.traverseUpTo(level);
            }
        }

        return null;
    }

    @Override
    public void collectIds(List<Id> targetList) {
        targetList.add(getId());

        NavigationItem p = getParent();

        if (p != null) {
            p.collectIds(targetList);
        }
    }

    @Override
    public void flatten(List<NavigationItem> targetList) {
        targetList.add(this);

        NavigationItem p = getParent();

        if (p != null) {
            p.flatten(targetList);
        }
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null)
            return;

        super.fromMap(map);

        this.id = id_(map.get(Col.ID));
        this.parentId = id_(map.get(Col.PARENT_ID));
        this.rootId = id_(map.get(Col.ROOT_ID));
        this.id2 = id_(map.get(Col.ID2));

        this.key = str_(map.get(Col.KEY));
        this.label = ctxObj_(map.get(Col.LABEL));
        this.position = int_(map.get(Col.POSITION), 0);
        this.level = int_(map.get(Col.LEVEL), 0);

        if (map.get(Col.TARGET_OBJECT_ID) != null)
            this.targetObjectId = id_(map.get(Col.TARGET_OBJECT_ID));

        this.targetObjectType = enum_(ObjectType.class, map.get(Col.TARGET_OBJECT_TYPE));

        this.useTargetObjectLabel = bool_(map.get(Col.TARGET_OBJECT_LABEL), true);

        if (map.get(Col.EXTERNAL_URL) != null)
            this.externalURL = ctxObj_(map.get(Col.EXTERNAL_URL));

        this.enabled = bool_(map.get(Col.ENABLED), false);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put(Col.ID, getId());

        if (getRootId() != null)
            map.put(Col.ROOT_ID, getRootId());

        if (getParentId() != null)
            map.put(Col.PARENT_ID, getParentId());

        if (getId2() != null)
            map.put(Col.ID2, getId2());

        map.put(Col.KEY, getKey());
        map.put(Col.LABEL, getLabel());
        map.put(Col.POSITION, getPosition());
        map.put(Col.LEVEL, getLevel());

        if (getTargetObjectId() != null)
            map.put(Col.TARGET_OBJECT_ID, getTargetObjectId());

        if (getTargetObjectType() != null)
            map.put(Col.TARGET_OBJECT_TYPE, getTargetObjectType().toId());

        map.put(Col.TARGET_OBJECT_LABEL, this.useTargetObjectLabel);

        if (getExternalURL() != null)
            map.put(Col.EXTERNAL_URL, getExternalURL());

        map.put(Col.ENABLED, isEnabled());

        return map;
    }

    @Override
    public String toString() {
        return "DefaultNavigationItem [id=" + id + ", parentId=" + parentId + ", rootId=" + rootId + ", id2=" + id2
            + ", key=" + key + ", label=" + label + ", level=" + level + ", position=" + position
            + ", targetObjectId=" + targetObjectId + ", targetObjectType=" + targetObjectType
            + ", useTargetObjectLabel=" + useTargetObjectLabel + ", externalURL=" + externalURL + ", enabled="
            + enabled + ", displayLabel=" + getDisplayLabel() + ", displayURI=" + getDisplayURI()
            + ", targetObject=" + getTargetObject() + ", parent=" + getParent() + "]";
    }
}
