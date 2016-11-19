package com.geecommerce.optivobroadmail.mailer.service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geecommerce.core.App;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.optivobroadmail.mailer.configuration.Configuration;
import com.geecommerce.optivobroadmail.mailer.configuration.TemplateParameter;
import com.geecommerce.optivobroadmail.mailer.exception.MailerServiceException;
import com.geecommerce.optivobroadmail.mailer.exception.RecipientListException;
import com.geecommerce.optivobroadmail.mailer.exception.SendMailException;
import com.google.inject.Inject;

@Service
public class DefaultOptivoMailerService implements OptivoMailerService {
    @Inject
    protected App app;

    protected static final Logger log = LogManager.getLogger(DefaultOptivoMailerService.class);

    public static final ContentType contentType = ContentType.create("application/x-www-form-urlencoded", App.get().cpStr_(Configuration.BM_ENCODING));

    public static final String CMD_SENDEVENTMAIL = "sendeventmail";
    public static final String CMD_SENDTRANSACTIONTMAIL = "sendtransactionmail";
    public static final String CMD_GETSENDSTATUS = "getsetndstatus";
    public static final String CMD_UPLOADPERSONALIZEDATTACHMENTS = "uploadpersonalizedattachments";
    public static final String CMD_SUBSCRIBE = "subscribe";
    public static final String CMD_UNSUBSCRIBE = "unsubscribe";
    public static final String CMD_UPDATEFIELDS = "updatefields";
    public static final String CMD_NOP = "nop";
    public static final String CMD_ONLINEVERSION = "onlineversion";

    @Inject
    public DefaultOptivoMailerService() {
    }

    @Override
    public String invoke(String operation, String authCode, Map<String, Object> parameters) throws MailerServiceException, IOException {
        final MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
        addSystemParameters(multipartEntity);

        if (parameters != null) {
            for (String p : parameters.keySet()) {
                Object o = parameters.get(p);
                if (o instanceof String) {
                    if (StringUtils.isNotBlank((String) o)) {
                        multipartEntity.addTextBody(p, (String) o, contentType);
                    }
                } else if (o instanceof Date) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String dataString = dateFormat.format((Date) o);
                    if (StringUtils.isNotBlank(dataString)) {
                        multipartEntity.addTextBody(p, dataString, contentType);
                    }
                } else if (o instanceof List) {
                    for (Object item : (List) o) {
                        if (item instanceof File) {
                            multipartEntity.addPart("bmFile", new FileBody((File) item));
                        }
                    }
                } else if (o instanceof Number) {
                    multipartEntity.addTextBody(p, String.valueOf(o), contentType);
                }
            }
        }

        HttpPost sendHttpPost = new HttpPost(buildServiceUri("form", authCode) + "/" + operation);
        sendHttpPost.setEntity(multipartEntity.build());
        HttpResponse sendResponse = createHttpClient().execute(sendHttpPost);

        HttpEntity entity = sendResponse.getEntity();
        String response = IOUtils.toString(entity.getContent(), "UTF-8");
        if (sendResponse.getStatusLine().getStatusCode() != 200) {
            throw new MailerServiceException(response);
        }

        EntityUtils.consume(entity);
        return response;
    }

    @Override
    public String sendEventMail(String recipientId) throws MailerServiceException, IOException {
        return sendEventMail(recipientId, app.cpStr_(Configuration.BM_MAILING_ID));
    }

    @Override
    public String sendEventMail(String recipientId, String mailingId) throws MailerServiceException, IOException {
        return send(CMD_SENDEVENTMAIL, recipientId, mailingId, null, null);
    }

    @Override
    public String sendMail(String key, String recipientId, Map<String, Object> recipientParams) {
        try {
            String mailingId = app.cpStr_(key);

            // for (String s : recipientParams.keySet())
            // {
            // Object o = recipientParams.get(s);
            // if (o instanceof String)
            // {
            // String encoded = URLEncoder.encode((String)o,
            // app.cpStr_(Configuration.BM_ENCODING));
            // recipientParams.put(s, encoded);
            // }
            // }

            return sendTransactionMail(recipientId, mailingId, null, recipientParams);
        } catch (MailerServiceException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    @Override
    public String sendTransactionMail(String recipientId, String attachmentsToken, Map<String, Object> recipientParams) throws MailerServiceException, IOException {
        return sendTransactionMail(recipientId, app.cpStr_(Configuration.BM_MAILING_ID), attachmentsToken, recipientParams);
    }

    @Override
    public String sendTransactionMail(String recipientId, String mailingId, String attachmentToken, Map<String, Object> recipientParams) throws MailerServiceException, IOException {
        return send(CMD_SENDTRANSACTIONTMAIL, recipientId, mailingId, attachmentToken, recipientParams);
    }

    private String send(String command, String recipientId, String mailingId, String attachmentToken, Map<String, Object> additionalParams) throws MailerServiceException, IOException {

        Map<String, Object> params = new LinkedHashMap<>();
        if (StringUtils.isNotBlank(recipientId)) {
            params.put("bmRecipientId", recipientId);
        }
        if (StringUtils.isNotBlank(attachmentToken)) {
            params.put("bmPersonalizedAttachmentsToken", attachmentToken);
        }

        params.put("bmMailingId", mailingId);

        if (additionalParams != null) {
            additionalParams.keySet().stream().filter(p -> additionalParams.get(p) != null).forEach(p -> params.put(p, additionalParams.get(p)));
        }

        String response = invoke(command, app.cpStr_(Configuration.BM_MAILSERVICE_AUTH_CODE), params);
        if (!response.startsWith("enqueued:")) {
            throw new SendMailException(response);
        } else {
            log.debug("Optivo mail  has been sent. Username='" + recipientId + "' mailingId=" + mailingId + " response=" + response);
            System.out.println("Optivo mail  has been sent. Username='" + recipientId + "' mailingId=" + mailingId + " response=" + response);

            // to archive
            if (app.cpBool_(Configuration.SEND_TO_ARCHIVE)) {
                params.put("bmRecipientId", app.cpStr_(Configuration.ARCHIVE_MAILBOX));
                params.put(TemplateParameter.KDKONTO, recipientId);
                String archiveResponse = invoke(command, app.cpStr_(Configuration.BM_MAILSERVICE_AUTH_CODE), params);
                System.out.println("EMail for '" + recipientId + "' mailingId=" + mailingId + " has been sent to archive. Response=" + archiveResponse);
            }

            return response.replace("enqueued: ", "");
        }
    }

    @Override
    public String getSendStatus(String mailId) throws MailerServiceException, IOException {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("bmMailId", mailId);
        return invoke(CMD_GETSENDSTATUS, app.cpStr_(Configuration.BM_MAILSERVICE_AUTH_CODE), params);
    }

    @Override
    public String subscribe(String recipientId, String optinId, boolean failOnUnsubscribe, boolean overwrite, String optinSource, Map<String, String> additionalParams)
        throws MailerServiceException, IOException {
        Map<String, Object> params = new LinkedHashMap<>();
        if (StringUtils.isNotBlank(recipientId)) {
            params.put("bmRecipientId", recipientId);
        }
        if (StringUtils.isNotBlank(optinId)) {
            params.put("bmOptInId", app.cpStr_(optinId));
        }

        params.put("bmFailOnUnsubscribe", String.valueOf(failOnUnsubscribe));
        params.put("bmOverwrite", String.valueOf(overwrite));

        if (StringUtils.isNotBlank(optinSource)) {
            params.put("bmOptinSource", optinSource);
        }

        if (additionalParams != null) {
            additionalParams.keySet().stream().filter(p -> StringUtils.isNotBlank(additionalParams.get(p))).forEach(p -> params.put(p, additionalParams.get(p)));
        }

        String response = invoke(CMD_SUBSCRIBE, app.cpStr_(Configuration.BM_NEWSLETTER_AUTH_CODE_1), params);
        if (!response.startsWith("ok")) {
            throw new RecipientListException(response);
        } else {
            return response;
        }
    }

    @Override
    public String unsubscribe(String recipientId, String mailId, boolean removeId) throws MailerServiceException, IOException {

        Map<String, Object> params = new LinkedHashMap<>();
        if (StringUtils.isNotBlank(recipientId)) {
            params.put("bmRecipientId", recipientId);
        }
        if (StringUtils.isNotBlank(mailId)) {
            params.put("bmMailId", mailId);
        }

        params.put("bmRemoveId", String.valueOf(removeId));

        String response = invoke(CMD_UNSUBSCRIBE, app.cpStr_(Configuration.BM_NEWSLETTER_AUTH_CODE_1), params);
        if (!response.startsWith("ok")) {
            throw new RecipientListException(response);
        } else {
            return response;
        }
    }

    @Override
    public String updateFields(String recipientId, String newRecipientId, boolean overwrite, Map<String, String> additionalParams) throws MailerServiceException, IOException {

        Map<String, Object> params = new LinkedHashMap<>();
        if (StringUtils.isNotBlank(recipientId)) {
            params.put("bmRecipientId", recipientId);
        }
        if (StringUtils.isNotBlank(newRecipientId)) {
            params.put("bmNewRecipientId", newRecipientId);
        }

        params.put("bmOverwrite", String.valueOf(overwrite));
        if (additionalParams != null) {
            additionalParams.keySet().stream().filter(p -> StringUtils.isNotBlank(additionalParams.get(p))).forEach(p -> params.put(p, additionalParams.get(p)));
        }

        String response = invoke(CMD_UPDATEFIELDS, app.cpStr_(Configuration.BM_NEWSLETTER_AUTH_CODE_1), params);
        if (!response.startsWith("ok")) {
            throw new RecipientListException(response);
        } else {
            return response;
        }
    }

    @Override
    public String uploadAttacments(List<File> attachments) throws MailerServiceException, IOException {
        if (attachments == null || attachments.size() > 5) {
            throw new MailerServiceException("Attachments are absent or too much number to be upload.");
        }

        final MultipartEntityBuilder uploadMultipartEntity = MultipartEntityBuilder.create();
        addSystemParameters(uploadMultipartEntity);

        Map<String, Object> params = new LinkedHashMap<>();
        List<File> objects = Collections.emptyList();
        attachments.stream().forEach(attachment -> objects.add(attachment));
        params.put("bmFile", objects);

        String response = invoke(CMD_UPLOADPERSONALIZEDATTACHMENTS, app.cpStr_(Configuration.BM_MAILSERVICE_AUTH_CODE), params);
        if (!response.startsWith("ok:")) {
            throw new MailerServiceException(response);
        } else {
            return response.replace("ok: ", "");
        }
    }

    @Override
    public String nop(String parameter, String value) throws IOException, MailerServiceException {
        Map<String, Object> params = new LinkedHashMap<>();
        if (StringUtils.isNotBlank(parameter)) {
            params.put(parameter, value);
        }
        return invoke(CMD_NOP, app.cpStr_(Configuration.BM_MAILSERVICE_AUTH_CODE), params);
    }

    @Override
    public String onlineVersion(String recipientId, String pattern, String mailingId) throws IOException, MailerServiceException {
        Map<String, Object> params = new LinkedHashMap<>();
        if (StringUtils.isNotBlank(recipientId)) {
            params.put("bmRecipientId", recipientId);
        }
        if (StringUtils.isNotBlank(pattern)) {
            params.put("bmPattern", pattern);
        }
        if (StringUtils.isNotBlank(mailingId)) {
            params.put("bmMailingId", mailingId);
        }
        return invoke(CMD_ONLINEVERSION, app.cpStr_(Configuration.BM_MAILSERVICE_AUTH_CODE), params);
    }

    private HttpClient createHttpClient() {
        return HttpClientBuilder.create().build();
    }

    private String buildServiceUri(String serviceType, String authCode) throws UnsupportedEncodingException {

        return app.cpStr_(Configuration.BM_MAILSERVICE_PROTOCOL) + "://" + app.cpStr_(Configuration.BM_MAILSERVICE_HOST) + "/http" + "/" + serviceType + "/" + authCode;
    }

    private void addSystemParameters(MultipartEntityBuilder builder) {
        String bmSuccessUrl = app.cpStr_(Configuration.BM_SUCCESS_URL);
        if (StringUtils.isNotBlank(bmSuccessUrl)) {
            builder.addTextBody("bmSuccessUrl", bmSuccessUrl, contentType);
        }

        String bmFailureUrl = app.cpStr_(Configuration.BM_FAILURE_URL);
        if (StringUtils.isNotBlank(bmFailureUrl)) {
            builder.addTextBody("bmFailureUrl", bmFailureUrl, contentType);
        }

        String bmUrl = app.cpStr_(Configuration.BM_URL);
        if (StringUtils.isNotBlank(bmUrl)) {
            builder.addTextBody("bmUrl", bmUrl, contentType);
        }

        String bmEncoding = app.cpStr_(Configuration.BM_ENCODING);
        if (StringUtils.isNotBlank(bmEncoding)) {
            builder.addTextBody("bmEncoding", bmEncoding, contentType);
        }

        String bmVerbose = app.cpStr_(Configuration.BM_VERBOSE);
        if (StringUtils.isNotBlank(bmVerbose)) {
            builder.addTextBody("bmVerbose", bmVerbose, contentType);
        }
    }
}
