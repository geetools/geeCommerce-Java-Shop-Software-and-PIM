package com.geecommerce.antispam.service;

import com.geecommerce.antispam.model.RequestCounter;
import com.geecommerce.core.service.api.Service;

public interface RequestCounterService extends Service {

    RequestCounter createCounter(String name, String ip);

    void increaseCounter(RequestCounter requestCounter);

    void resetCounter(RequestCounter requestCounter);

    void blockCounter(RequestCounter requestCounter);
}
