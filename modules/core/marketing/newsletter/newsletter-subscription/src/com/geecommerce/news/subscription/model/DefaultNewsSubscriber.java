package com.geecommerce.news.subscription.model;

import java.util.HashSet;
import java.util.Set;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.service.api.GlobalColumn;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.type.Id;

@Model("news_subscribers")
public class DefaultNewsSubscriber extends AbstractModel implements NewsSubscriber {
    private static final long serialVersionUID = 1987242562690159101L;

    @Column(Col.ID)
    private Id id;

    @Column(Col.EMAIL)
    private String email;

    @Column(Col.SUBSCRIBED)
    private Boolean subscribed;

    @Column(Col.SOURCE)
    private String source;

    @Column(Col.SOURCE_ID)
    private Id sourceId;

    @Column(GlobalColumn.MERCHANT_ID)
    protected Set<Id> merchantIds = null;

    @Column(GlobalColumn.STORE_ID)
    protected Set<Id> storeIds = null;

    @Column(GlobalColumn.REQUEST_CONTEXT_ID)
    private Set<Id> requestContextIds = null;

    @Override
    public Id getId() {
	return id;
    }

    @Override
    public NewsSubscriber setId(Id id) {
	this.id = id;
	return this;
    }

    @Override
    public String getEmail() {
	return email;
    }

    @Override
    public NewsSubscriber setEmail(String email) {
	this.email = email;
	return this;
    }

    @Override
    public boolean isSubscribed() {
	return subscribed == null ? false : subscribed.booleanValue();
    }

    @Override
    public Boolean getSubscribed() {
	return subscribed;
    }

    @Override
    public NewsSubscriber setSubscribed(Boolean subscribed) {
	this.subscribed = subscribed;
	return this;
    }

    @Override
    public String getSource() {
	return source;
    }

    @Override
    public NewsSubscriber setSource(String source) {
	this.source = source;
	return this;
    }

    @Override
    public Id getSourceId() {
	return sourceId;
    }

    @Override
    public NewsSubscriber setSourceId(Id sourceId) {
	this.sourceId = sourceId;
	return this;
    }

    @Override
    public Set<Id> getMerchantIds() {
	return merchantIds;
    }

    @Override
    public NewsSubscriber addTo(Merchant merchant) {
	if (merchant == null || merchant.getId() == null)
	    throw new IllegalStateException("Merchant cannot be null");

	if (merchantIds == null)
	    merchantIds = new HashSet<>();

	merchantIds.add(merchant.getId());
	return this;
    }

    @Override
    public boolean isIn(Merchant merchant) {
	if (merchant == null || merchant.getId() == null)
	    return false;

	if (merchantIds == null || merchantIds.isEmpty())
	    return false;

	return merchantIds.contains(merchant.getId());
    }

    @Override
    public Set<Id> getStoreIds() {
	return storeIds;
    }

    @Override
    public NewsSubscriber addTo(Store store) {
	if (store == null || store.getId() == null)
	    throw new IllegalStateException("Store cannot be null");

	if (storeIds == null)
	    storeIds = new HashSet<>();

	storeIds.add(store.getId());
	return this;
    }

    @Override
    public boolean isIn(Store store) {
	if (store == null || store.getId() == null)
	    return false;

	if (storeIds == null || storeIds.isEmpty())
	    return false;

	return storeIds.contains(store.getId());
    }

    @Override
    public Set<Id> getRequestContextIds() {
	return requestContextIds;
    }

    @Override
    public NewsSubscriber addTo(RequestContext requestContext) {
	if (requestContext == null || requestContext.getId() == null)
	    throw new IllegalStateException("RequestContext cannot be null");

	if (requestContextIds == null)
	    requestContextIds = new HashSet<>();

	requestContextIds.add(requestContext.getId());
	return this;
    }

    @Override
    public boolean isIn(RequestContext requestContext) {
	if (requestContext == null || requestContext.getId() == null)
	    return false;

	if (requestContextIds == null || requestContextIds.isEmpty())
	    return false;

	return requestContextIds.contains(requestContext.getId());
    }

}
