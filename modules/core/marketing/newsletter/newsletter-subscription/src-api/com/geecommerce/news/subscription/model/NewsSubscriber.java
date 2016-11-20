package com.geecommerce.news.subscription.model;

import java.util.Set;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.type.Id;

public interface NewsSubscriber extends Model {
    public Id getId();

    public NewsSubscriber setId(Id id);

    public String getEmail();

    public NewsSubscriber setEmail(String email);

    public boolean isSubscribed();

    public Boolean getSubscribed();

    public NewsSubscriber setSubscribed(Boolean subscribed);

    public String getSource();

    public NewsSubscriber setSource(String source);

    public Id getSourceId();

    public NewsSubscriber setSourceId(Id sourceId);

    public Set<Id> getMerchantIds();

    public NewsSubscriber addTo(Merchant merchant);

    public boolean isIn(Merchant merchant);

    public Set<Id> getStoreIds();

    public NewsSubscriber addTo(Store store);

    public boolean isIn(Store store);

    public Set<Id> getRequestContextIds();

    public NewsSubscriber addTo(RequestContext requestContext);

    public boolean isIn(RequestContext requestContext);

    static final class Col {
        public static final String ID = "_id";
        public static final String EMAIL = "email";
        public static final String SUBSCRIBED = "subscribed";
        public static final String SOURCE = "src";
        public static final String SOURCE_ID = "src_id";
    }
}
