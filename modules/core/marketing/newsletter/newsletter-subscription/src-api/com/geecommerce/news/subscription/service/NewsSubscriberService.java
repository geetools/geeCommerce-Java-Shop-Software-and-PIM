package com.geecommerce.news.subscription.service;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.type.Id;

public interface NewsSubscriberService extends Service {

    public void subscribe(String email, String source, Id sourceId);

    public void unsubscribe(String email, String source, Id sourceId);
}
