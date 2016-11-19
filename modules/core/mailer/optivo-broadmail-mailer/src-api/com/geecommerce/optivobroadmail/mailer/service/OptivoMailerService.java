package com.geecommerce.optivobroadmail.mailer.service;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.optivobroadmail.mailer.exception.MailerServiceException;
import com.geecommerce.optivobroadmail.mailer.exception.RecipientListException;
import com.geecommerce.optivobroadmail.mailer.exception.SendMailException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface OptivoMailerService extends Service {
    String invoke(String operation, String authCode, Map<String, Object> parameters) throws MailerServiceException, IOException;

    String sendEventMail(String recipientId) throws MailerServiceException, IOException;

    String sendEventMail(String recipientId, String mailingId) throws MailerServiceException, IOException;

    String sendMail(String key, String recipientId, Map<String, Object> recipientParams);

    String sendTransactionMail(String recipientId, String attachmentToken, Map<String, Object> recipientParams) throws MailerServiceException, IOException;

    String sendTransactionMail(String recipientId, String mailingId, String attachmentsToken, Map<String, Object> recipientParams) throws MailerServiceException, IOException;

    String getSendStatus(String mailId) throws MailerServiceException, IOException;

    String subscribe(String recipientId, String optinId, boolean failOnUnsubscribe, boolean overwrite, String optinSource, Map<String, String> params) throws MailerServiceException, IOException;

    String unsubscribe(String recipientId, String mailId, boolean removeId) throws MailerServiceException, IOException;

    String updateFields(String recipientId, String newRecipientId, boolean overwrite, Map<String, String> params) throws MailerServiceException, IOException;

    String uploadAttacments(List<File> attachments) throws MailerServiceException, IOException;

    String nop(String parameter, String value) throws IOException, MailerServiceException;

    String onlineVersion(String recipientId, String pattern, String mailingId) throws IOException, MailerServiceException;

}
