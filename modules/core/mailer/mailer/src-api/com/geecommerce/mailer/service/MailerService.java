package com.geecommerce.mailer.service;

import java.util.Map;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.type.Id;
import com.geecommerce.mailer.model.MailerTemplate;

public interface MailerService extends Service {
    public MailerTemplate createMailerTemplate(MailerTemplate mailerTemplate);

    public void removeMailerTemplate(MailerTemplate mailerTemplate);

    public void updateMailerTemplate(MailerTemplate mailerTemplate);

    public MailerTemplate getMailerTemplate(Id mailerTemplateId);

    public void sendMail(String key, String to, Map<String, Object> params, String smtpConfigKey);

    public void sendMail(String key, String to, Map<String, Object> params);

    public MailerTemplate getMailerTemplateByKey(String key);

}
