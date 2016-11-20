package com.geecommerce.news.subscription.controller;

import com.geecommerce.antispam.helper.AntiSpamHelper;
import com.geecommerce.core.message.Context;
import com.geecommerce.core.message.ResponseListener;
import com.geecommerce.core.web.BaseActionBean;
import com.geecommerce.news.subscription.service.NewsSubscriberService;
import com.google.inject.Inject;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

@UrlBinding("/news-subscription/{$event}/{id}")
public class NewsSubscriptionAction extends BaseActionBean {

    private String email;
    private String phone;

    private String bmrecipientid;

    private final NewsSubscriberService newsSubscriberService;
    private final AntiSpamHelper antiSpamHelper;

    @Inject
    public NewsSubscriptionAction(NewsSubscriberService newsSubscriberService, AntiSpamHelper antiSpamHelper) {
        this.newsSubscriberService = newsSubscriberService;
        this.antiSpamHelper = antiSpamHelper;
    }

    @DefaultHandler
    @HandlesEvent("subscribe")
    public Resolution subscribe() {
        if (!antiSpamHelper.checkRequestCounter("contact")) {
            return json("{}");
        }

        System.out.println("[Newsletter subscription] = " + email);
        if (email != null) {
            newsSubscriberService.subscribe(email, "NEWS_SUBSCR", null);
        }

        app.publish("newsletter:subscription",
            Context.create("email", email, "action", "subscribe").setResponseListener(new ResponseListener() {
                @Override
                public void onResponse(Object response) {
                }
            }));

        return json("{}");
    }

    @HandlesEvent("unsubscribe")
    public Resolution unsubscribe() {
        if (!antiSpamHelper.checkRequestCounter("contact")) {
            return json("{}");
        }

        invokeUnsubscribe();

        return json("{}");
    }

    @HandlesEvent("unsubscribe-landing")
    public Resolution unsubscribeWithLanding() {
        if (!antiSpamHelper.checkRequestCounter("contact")) {
            return json("{}");
        }

        invokeUnsubscribe();

        return view("static/newsletter_unsubscribe_landing");
    }

    private void invokeUnsubscribe() {
        System.out.println("[Newsletter unsubscribe] = " + email);
        if (email != null) {
            newsSubscriberService.unsubscribe(email, "NEWS_SUBSCR", null);
        }

        app.publish("newsletter:subscription",
            Context.create("email", email, "action", "unsubscribe").setResponseListener(new ResponseListener() {
                @Override
                public void onResponse(Object response) {
                }
            }));
    }

    @HandlesEvent("sms-service")
    public Resolution smsServiceSubscribe() {
        if (!antiSpamHelper.checkRequestCounter("contact")) {
            return json("{}");
        }

        System.out.println("[SMS service subscription] = " + phone);
        if (email != null) {
            newsSubscriberService.subscribe(email, "SMS_SUBSCR", null);
        }

        app.publish("sms-service:subscription", "phone", phone);

        return json("{}");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBmrecipientid() {
        return bmrecipientid;
    }

    public void setBmrecipientid(String bmrecipientid) {
        this.bmrecipientid = bmrecipientid;
        this.email = bmrecipientid;
    }
}
