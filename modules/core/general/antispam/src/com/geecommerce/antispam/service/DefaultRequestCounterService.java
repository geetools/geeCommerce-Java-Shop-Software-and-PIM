package com.geecommerce.antispam.service;

import java.util.Date;

import com.geecommerce.antispam.model.RequestCounter;
import com.geecommerce.antispam.repository.RequestCounters;
import com.geecommerce.core.App;
import com.geecommerce.core.service.annotation.Service;
import com.google.inject.Inject;

@Service
public class DefaultRequestCounterService implements RequestCounterService {
    @Inject
    protected App app;

    protected final RequestCounters requestCounters;

    @Inject
    public DefaultRequestCounterService(RequestCounters requestCounters) {
        this.requestCounters = requestCounters;
    }

    @Override
    public RequestCounter createCounter(String name, String ip) {
        RequestCounter requestCounter = app.getModel(RequestCounter.class);
        requestCounter.setName(name);
        requestCounter.setIp(ip);
        requestCounter.setRequestTime(new Date());
        requestCounter.setCount(1);
        requestCounter.setBlocked(false);
        return requestCounters.add(requestCounter);
    }

    @Override
    public void increaseCounter(RequestCounter requestCounter) {
        requestCounter.setCount(requestCounter.getCount() + 1);
        requestCounters.update(requestCounter);
    }

    @Override
    public void resetCounter(RequestCounter requestCounter) {
        requestCounter.setBlocked(false);
        requestCounter.setCount(1);
        requestCounter.setRequestTime(new Date());
        requestCounters.update(requestCounter);
    }

    @Override
    public void blockCounter(RequestCounter requestCounter) {
        requestCounter.setBlocked(true);
        requestCounter.setRequestTime(new Date());
        requestCounters.update(requestCounter);
    }
}
