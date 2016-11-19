package com.geecommerce.mailer.repository;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.mailer.model.MailerTemplate;

public interface MailerTemplates extends Repository {
    public MailerTemplate thatBelongTo(String key);
}
