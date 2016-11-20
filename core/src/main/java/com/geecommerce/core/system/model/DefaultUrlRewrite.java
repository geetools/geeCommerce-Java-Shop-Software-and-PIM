package com.geecommerce.core.system.model;

import java.util.List;

import com.geecommerce.core.enums.ObjectType;
import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.owlike.genson.annotation.JsonIgnore;

@Cacheable
@Model("url_rewrites")
public class DefaultUrlRewrite extends AbstractMultiContextModel implements UrlRewrite {
    private static final long serialVersionUID = -1178652228401482784L;

    @Column(Col.ID)
    private Id id = null;

    @Column(Col.ID2)
    private Id id2 = null;

    @Column(Col.REQUEST_URI)
    private ContextObject<String> requestURI = null;

    @Column(Col.REQUEST_METHOD)
    private String requestMethod = null;

    @Column(Col.TARGET_URL)
    private String targetURL = null;

    @Column(Col.TARGET_OBJECT_ID)
    private Id targetObjectId = null;

    @Column(Col.TARGET_OBJECT_TYPE)
    private ObjectType targetObjectType = null;

    @Column(Col.FLAGS)
    private List<String> flags = null;

    @Column(Col.MANUAL_REWRITE)
    private boolean manual = false;

    @Column(Col.ENABLED)
    private boolean enabled = false;

    public DefaultUrlRewrite() {
        super();
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public UrlRewrite setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public Id getId2() {
        return id2;
    }

    @Override
    public UrlRewrite setId2(Id id2) {
        this.id2 = id2;
        return this;
    }

    @Override
    public ContextObject<String> getRequestURI() {
        return requestURI;
    }

    @Override
    public UrlRewrite setRequestURI(ContextObject<String> requestURI) {
        this.requestURI = requestURI;
        return this;
    }

    @Override
    public String getRequestMethod() {
        return requestMethod;
    }

    @Override
    public UrlRewrite setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }

    @Override
    public String getTargetURL() {
        return targetURL;
    }

    @Override
    public UrlRewrite setTargetURL(String targetURL) {
        this.targetURL = targetURL;
        return this;
    }

    @Override
    public Id getTargetObjectId() {
        return targetObjectId;
    }

    @Override
    public UrlRewrite setTargetObjectId(Id targetObjectId) {
        this.targetObjectId = targetObjectId;
        return this;
    }

    @Override
    public ObjectType getTargetObjectType() {
        return targetObjectType;
    }

    @Override
    public UrlRewrite setTargetObjectType(ObjectType targetObjectType) {
        this.targetObjectType = targetObjectType;
        return this;
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
    public boolean hasTargetUrl() {
        return !isForProductList() && !isForProduct() && !isForCMS() && targetURL != null;
    }

    @Override
    public List<String> getFlags() {
        return flags;
    }

    @Override
    public UrlRewrite setFlags(List<String> flags) {
        this.flags = flags;
        return this;
    }

    @Override
    public boolean isManual() {
        return manual;
    }

    @Override
    public UrlRewrite setManual(boolean manual) {
        this.manual = manual;
        return this;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public UrlRewrite setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}
