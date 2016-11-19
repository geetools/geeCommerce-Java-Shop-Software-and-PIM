package com.geecommerce.news.subscription.repository;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.news.subscription.model.NewsSubscriber;

public interface NewsSubscribers extends Repository {
    public NewsSubscriber thatBelongTo(String email, Store store);
}
