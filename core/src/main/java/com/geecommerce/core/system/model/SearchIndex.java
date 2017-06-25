package com.geecommerce.core.system.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public interface SearchIndex extends Model {

    public Id getId();

    public SearchIndex setId(Id id);

    public Id getMerchantId();

    public SearchIndex setMerchantId(Id merchantId);

    public Id getStoreId();

    public SearchIndex setStoreId(Id storeId);

    public Id getRequestContextId();

    public SearchIndex setRequestContextId(Id requestContextId);

    public boolean getEnabled();

    public SearchIndex setEnabled(boolean enabled);

    static final class Col {
        public static final String ID = "_id";
        public static final String MERCHANT_ID = "m";
        public static final String STORE_ID = "s";
        public static final String REQUEST_CONTEXT_ID = "req_ctx_id";

        public static final String ENABLED = "enabled";
    }
}
