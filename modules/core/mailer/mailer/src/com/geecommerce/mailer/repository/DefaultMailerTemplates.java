package com.geecommerce.mailer.repository;

import java.util.HashMap;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.mailer.model.MailerTemplate;

@Repository
public class DefaultMailerTemplates extends AbstractRepository implements MailerTemplates {
    @Override
    public MailerTemplate thatBelongTo(String key) {
        if (key == null || key.isEmpty())
            return null;

        Map<String, Object> filter = new HashMap<>();
        filter.put(MailerTemplate.Column.KEY, key);
        return multiContextFindOne(MailerTemplate.class, filter);
    }
}
