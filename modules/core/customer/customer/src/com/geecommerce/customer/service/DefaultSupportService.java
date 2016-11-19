package com.geecommerce.customer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.Str;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.mailer.service.MailerService;
import com.google.inject.Inject;

@Service
public class DefaultSupportService implements SupportService {
    @Inject
    protected App app;

    protected final static String FAQ_MAIL_ADDRESS = "faq/mail/address";

    protected final MailerService mailerService;

    @Inject
    public DefaultSupportService(MailerService mailerService) {
        this.mailerService = mailerService;
    }

    @Override
    public void sendSupportMail(String questionerEmail, String question) {
        Map<String, Object> templateParams = new HashMap<>();
        templateParams.put("questionerEmail", questionerEmail);
        templateParams.put("question", question);
        templateParams.put("ipAddress", app.getClientIpAddress());

        List<String> supportEmails = app.cpStrList_(FAQ_MAIL_ADDRESS);

        if (supportEmails != null && !supportEmails.isEmpty()) {
            for (String supportEmail : supportEmails) {
                if (!Str.isEmpty(supportEmail)) {
                    System.out.println("Sending faq contact form to email: [supportEmail=" + supportEmail + "].");

                    try {
                        mailerService.sendMail("faq_question_template", supportEmail, templateParams);
                    } catch (Throwable t) {

                    }
                }
            }
        }
    }

    public String getConfigProperty(String configPropertyName) {
        return app.cpStr_(configPropertyName);
    }
}
