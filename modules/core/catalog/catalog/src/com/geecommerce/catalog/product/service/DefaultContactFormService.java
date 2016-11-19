package com.geecommerce.catalog.product.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.Str;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.mailer.service.MailerService;
import com.google.inject.Inject;

@Service
public class DefaultContactFormService implements ContactFormService {
    @Inject
    protected App app;

    protected final static String CONTACT_FORM_MAIL_ADDRESS = "contact/form/mail/address";

    protected final MailerService mailerService;

    @Inject
    public DefaultContactFormService(MailerService mailerService) {
        this.mailerService = mailerService;
    }

    @Override
    public void sendSupportMail(String questionerEmail, String question, String article, String fullName) {
        Map<String, Object> templateParams = new HashMap<>();
        templateParams.put("questionerEmail", String.valueOf(questionerEmail));
        templateParams.put("question", String.valueOf(question));
        templateParams.put("article", String.valueOf(article));
        templateParams.put("fullName", String.valueOf(fullName));

        List<String> supportEmails = app.cpStrList_(CONTACT_FORM_MAIL_ADDRESS);

        if (supportEmails != null && !supportEmails.isEmpty()) {
            for (String supportEmail : supportEmails) {
                if (!Str.isEmpty(supportEmail)) {
                    System.out.println("Sending contact form to email: [supportEmail=" + supportEmail + "].");

                    try {
                        mailerService.sendMail("contact_form_question_template", supportEmail, templateParams);
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
