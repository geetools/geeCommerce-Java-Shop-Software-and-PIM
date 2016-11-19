package com.geecommerce.antispam.repository;

import java.util.HashMap;
import java.util.Map;

import com.geecommerce.antispam.model.RequestCounter;
import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;

@Repository
public class DefaultRequestCounters extends AbstractRepository implements RequestCounters {
    @Override
    public RequestCounter withNameAndIp(String name, String ip) {
        if (name == null || ip == null)
            return null;

        Map<String, Object> filter = new HashMap<>();
        filter.put(RequestCounter.Col.NAME, name);
        filter.put(RequestCounter.Col.IP, ip);

        return findOne(RequestCounter.class, filter);
    }
}
