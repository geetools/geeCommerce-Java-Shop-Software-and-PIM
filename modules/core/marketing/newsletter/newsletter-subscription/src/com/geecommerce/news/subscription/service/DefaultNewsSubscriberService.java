package com.geecommerce.news.subscription.service;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.type.Id;
import com.geecommerce.news.subscription.model.NewsSubscriber;
import com.geecommerce.news.subscription.repository.NewsSubscribers;
import com.google.inject.Inject;

@Service
public class DefaultNewsSubscriberService implements NewsSubscriberService {
    @Inject
    protected App app;

    protected final NewsSubscribers newsSubscribers;

    @Inject
    public DefaultNewsSubscriberService(NewsSubscribers newsSubscribers) {
        this.newsSubscribers = newsSubscribers;
    }

    @Override
    public void subscribe(String email, String source, Id sourceId) {

        ApplicationContext appCtx = app.getApplicationContext();

        NewsSubscriber newsSubscriber = newsSubscribers.thatBelongTo(email, appCtx.getStore());
        if (newsSubscriber == null) {
            newsSubscriber = app.getModel(NewsSubscriber.class).setEmail(email).setSource(source).setSourceId(sourceId).addTo(appCtx.getMerchant()).addTo(appCtx.getStore())
                .addTo(appCtx.getRequestContext());
        }
        newsSubscriber.setSubscribed(true);

        if (newsSubscriber.getId() == null) {
            newsSubscribers.add(newsSubscriber);
        } else {
            newsSubscribers.update(newsSubscriber);
        }
    }

    @Override
    public void unsubscribe(String email, String source, Id sourceId) {
        ApplicationContext appCtx = app.getApplicationContext();

        NewsSubscriber newsSubscriber = newsSubscribers.thatBelongTo(email, appCtx.getStore());
        if (newsSubscriber == null) {
            newsSubscriber = app.getModel(NewsSubscriber.class).setEmail(email).setSource(source).setSourceId(sourceId).addTo(appCtx.getMerchant()).addTo(appCtx.getStore())
                .addTo(appCtx.getRequestContext());
        }
        newsSubscriber.setSubscribed(false);
        if (newsSubscriber.getId() == null) {
            newsSubscribers.add(newsSubscriber);
        } else {
            newsSubscribers.update(newsSubscriber);
        }
    }
}
