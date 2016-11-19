package com.geecommerce.core.type;

public interface ContextableObject {
    public Id getMerchantId();

    public Id getStoreId();

    public Id getRequestContextId();
}
