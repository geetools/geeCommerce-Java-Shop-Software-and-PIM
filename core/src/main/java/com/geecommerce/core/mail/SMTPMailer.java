package com.geecommerce.core.mail;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.URLDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geecommerce.core.App;
import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.media.MimeType;
import com.geecommerce.core.system.model.ConfigurationProperty;
import com.geecommerce.core.util.Strings;
import com.sun.istack.ByteArrayDataSource;
import com.sun.mail.util.MailSSLSocketFactory;

public class SMTPMailer implements Mailer {
    protected static final String CP_MAIL_PROTOCOL = "core/general/mail.transport.protocol";
    protected static final String CP_MAIL_HOST = "core/general/mail.smtp.host";
    protected static final String CP_MAIL_PORT = "core/general/mail.smtp.port";
    protected static final String CP_MAIL_FROM = "core/general/mail.smtp.from";
    protected static final String CP_MAIL_AUTH = "core/general/mail.smtp.auth";
    protected static final String CP_MAIL_USERNAME = "core/general/mail.smtp.username";
    protected static final String CP_MAIL_PASSWORD = "core/general/mail.smtp.password";
    protected static final String CP_MAIL_ENCRYPTION = "core/general/mail.smtp.encryption";
    protected static final String CP_MAIL_DEBUG = "core/general/mail.smtp.debug";

    protected static final String ENCRYPTION_SSL = "ssl";
    protected static final String DEFAULT_MIME_TYPE = "*/*";

    protected static final Logger log = LogManager.getLogger(SMTPMailer.class);

    @Override
    public void sendStreams(String subject, String bodyHtml, String bodyText, String to, String smtpConfigKey, Map<String, InputStream> attachments, Map<String, InputStream> inlineImages) {
        Map<String, byte[]> isAttachments = new LinkedHashMap<>();

        // set attachments
        if (attachments != null && attachments.size() > 0) {
            Set<String> keys = attachments.keySet();

            for (String key : keys) {
                if (key == null)
                    continue;

                InputStream is = attachments.get(key);

                if (is != null) {
                    try {
                        isAttachments.put(key, IOUtils.toByteArray(is));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        Map<String, byte[]> isInlineImages = new LinkedHashMap<>();

        // set inline images
        if (inlineImages != null && inlineImages.size() > 0) {
            Set<String> keys = inlineImages.keySet();

            for (String key : keys) {
                if (key == null)
                    continue;

                InputStream is = inlineImages.get(key);

                if (is != null) {
                    try {
                        isInlineImages.put(key, IOUtils.toByteArray(is));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        send(subject, bodyHtml, bodyText, to, smtpConfigKey, isAttachments, isInlineImages);
    }

    @Override
    public void send(String subject, String bodyHtml, String bodyText, String to, String smtpConfigKey, Map<String, byte[]> attachments, Map<String, byte[]> inlineImages) {
        Map<String, DataHandler> dhAttachments = new LinkedHashMap<>();

        // set attachments
        if (attachments != null && attachments.size() > 0) {
            Set<String> keys = attachments.keySet();

            for (String key : keys) {
                if (key == null)
                    continue;

                String name = null;
                String mimeType = null;
                byte[] bytes = attachments.get(key);

                if (bytes == null || bytes.length == 0)
                    continue;

                if (key.contains(Str.SEMI_COLON)) {
                    String[] sp = key.split(Str.SEMI_COLON);
                    name = sp[0].trim();
                    mimeType = sp[1].trim();
                } else {
                    name = key;
                }

                if (mimeType == null)
                    mimeType = MimeType.fromFilename(name);

                if (mimeType == null)
                    mimeType = DEFAULT_MIME_TYPE;

                DataSource uds = new ByteArrayDataSource(bytes, mimeType);
                dhAttachments.put(name, new DataHandler(uds));
            }
        }

        Map<String, DataHandler> dhInlineImages = new LinkedHashMap<>();

        // set inlineImages
        if (inlineImages != null && inlineImages.size() > 0) {
            Set<String> keys = inlineImages.keySet();

            for (String key : keys) {
                if (key == null)
                    continue;

                String name = null;
                String mimeType = null;
                byte[] bytes = inlineImages.get(key);

                if (bytes == null || bytes.length == 0)
                    continue;

                if (key.contains(Str.SEMI_COLON)) {
                    String[] sp = key.split(Str.SEMI_COLON);
                    name = sp[0].trim();
                    mimeType = sp[1].trim();
                } else {
                    name = key;
                }

                if (mimeType == null)
                    mimeType = MimeType.fromFilename(name);

                if (mimeType == null)
                    mimeType = DEFAULT_MIME_TYPE;

                DataSource uds = new ByteArrayDataSource(bytes, mimeType);
                dhInlineImages.put(name, new DataHandler(uds));
            }
        }

        sendMail(subject, bodyHtml, bodyText, to, smtpConfigKey, dhAttachments, dhInlineImages);
    }

    @Override
    public void send(String subject, String bodyHtml, String bodyText, String to, String smtpConfigKey, List<URL> attachments, List<URL> inlineImages) {
        Map<String, DataHandler> dhAttachments = new LinkedHashMap<>();

        // set attachments
        if (attachments != null && attachments.size() > 0) {
            for (URL attachment : attachments) {
                DataSource uds = new URLDataSource(attachment);
                dhAttachments.put(attachment.getFile(), new DataHandler(uds));
            }
        }

        Map<String, DataHandler> dhInlineImages = new LinkedHashMap<>();

        // set inlineImages
        if (inlineImages != null && inlineImages.size() > 0) {
            for (URL inlineImage : inlineImages) {
                DataSource uds = new URLDataSource(inlineImage);
                dhInlineImages.put(inlineImage.getFile(), new DataHandler(uds));
            }
        }

        sendMail(subject, bodyHtml, bodyText, to, smtpConfigKey, dhAttachments, dhInlineImages);
    }

    protected void sendMail(String subject, String bodyHtml, String bodyText, String to, String smtpConfigKey, Map<String, DataHandler> attachments, Map<String, DataHandler> inlineImages) {
        String protocol = null;
        String host = null;
        int port = -1;
        String from = null;
        boolean isAuth = true;
        String username = null;
        String password = null;
        String encryption = null;
        boolean isDebug = false;

        App app = App.get();

        if (smtpConfigKey != null && !smtpConfigKey.isEmpty()) {

            List<ConfigurationProperty> configProperties = app.getConfigProperties(smtpConfigKey);

            if (configProperties != null && !configProperties.isEmpty()) {
                for (ConfigurationProperty cp : configProperties) {
                    String key = cp.getKey();

                    if (key.contains("mail.transport.protocol")) {
                        protocol = app.cpStr_(CP_MAIL_PROTOCOL, cp.getStringValue());
                    } else if (key.contains("mail.smtp.host")) {
                        host = app.cpStr_(CP_MAIL_HOST, cp.getStringValue());
                    } else if (key.contains("mail.smtp.port")) {
                        port = app.cpInt_(CP_MAIL_PORT, cp.getIntegerValue());
                    } else if (key.contains("mail.smtp.from")) {
                        from = app.cpStr_(CP_MAIL_FROM, cp.getStringValue());
                    } else if (key.contains("mail.smtp.auth")) {
                        isAuth = app.cpBool_(CP_MAIL_AUTH, cp.getBooleanValue());
                    } else if (key.contains("mail.smtp.username")) {
                        username = app.cpStr_(CP_MAIL_USERNAME);
                    } else if (key.contains("mail.smtp.password")) {
                        password = app.cpStr_(CP_MAIL_PASSWORD);
                    } else if (key.contains("mail.smtp.encryption")) {
                        encryption = app.cpStr_(CP_MAIL_ENCRYPTION);
                    } else if (key.contains("mail.smtp.debug")) {
                        isDebug = app.cpBool_(CP_MAIL_DEBUG, false);
                    }
                }
            }

            if (protocol == null || protocol.isEmpty()) {
                protocol = app.cpStr_(CP_MAIL_PROTOCOL, "smtp");
            }

            if (host == null || host.isEmpty()) {
                host = app.cpStr_(CP_MAIL_HOST, "localhost");
            }

            if (port == -1) {
                port = app.cpInt_(CP_MAIL_PORT, 25);
            }

            if (from == null || from.isEmpty()) {
                from = app.cpStr_(CP_MAIL_FROM, "local@local");
            }

            if (username == null || username.isEmpty()) {
                username = app.cpStr_(CP_MAIL_USERNAME);
            }

            if (password == null || password.isEmpty()) {
                password = app.cpStr_(CP_MAIL_PASSWORD);
            }
        } else {
            protocol = app.cpStr_(CP_MAIL_PROTOCOL, "smtp");
            host = app.cpStr_(CP_MAIL_HOST, "localhost");
            port = app.cpInt_(CP_MAIL_PORT, 25);
            from = app.cpStr_(CP_MAIL_FROM, "local@local");
            isAuth = app.cpBool_(CP_MAIL_AUTH, false);
            username = app.cpStr_(CP_MAIL_USERNAME);
            password = app.cpStr_(CP_MAIL_PASSWORD);
            encryption = app.cpStr_(CP_MAIL_ENCRYPTION);
            isDebug = app.cpBool_(CP_MAIL_DEBUG, false);
        }

        // Get system properties
        Properties props = System.getProperties();

        // Setup mail server
        props.put("mail.transport.protocol", protocol);
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", String.valueOf(port));
        props.put("mail.smtp.auth", String.valueOf(isAuth));

        if (isDebug) {
            props.put("mail.debug", "true");
        }

        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");

        if (ENCRYPTION_SSL.equalsIgnoreCase(encryption)) {

            props.put("mail.smtp.ssl.enable", "true");
            // props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.socketFactory.fallback", "false");
            props.put("mail.smtp.socketFactory.port", port);
            try {
                MailSSLSocketFactory sf = new MailSSLSocketFactory();
                sf.setTrustAllHosts(true);
                props.put("mail.smtp.ssl.socketFactory", sf);
            } catch (Exception e) {
            }
        }

        Session session = null;

        if (isAuth) {
            Authenticator auth = new SMTPAuthenticator(username, password);
            session = Session.getInstance(props, auth);
        } else {
            session = Session.getInstance(props);
        }

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // TODO: Add reply-to configuration.
            // message.setReplyTo(InternetAddress.parse("some@address.com"));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message.setSubject(subject, "utf-8");

            // Set Multipart message
            Multipart mp = new MimeMultipart("alternative");

            if (bodyText != null) {
                MimeBodyPart bpText = new MimeBodyPart();
                bpText.setContent(bodyText, "text/plain;charset=\"UTF-8\"");
                mp.addBodyPart(bpText);
            }

            if (bodyHtml != null) {
                MimeBodyPart bpHtml = new MimeBodyPart();
                bpHtml.setContent(bodyHtml, "text/html;charset=\"UTF-8\"");
                mp.addBodyPart(bpHtml);
            }

            MimeBodyPart alternativeBp = new MimeBodyPart();
            alternativeBp.setContent(mp);

            Multipart finalMultipart = new MimeMultipart("related");
            finalMultipart.addBodyPart(alternativeBp);

            // set attachments
            if (attachments != null && attachments.size() > 0) {
                Set<String> keys = attachments.keySet();

                for (String key : keys) {
                    MimeBodyPart fileAttachmentPart = new MimeBodyPart();
                    fileAttachmentPart.setDataHandler(attachments.get(key));
                    fileAttachmentPart.setFileName(key);
                    fileAttachmentPart.setContentID("<" + getContentId(key) + ">");

                    System.out.println("1USING :::: " + getContentId(key));

                    finalMultipart.addBodyPart(fileAttachmentPart);
                }
            }

            // set inlineImages
            if (inlineImages != null && inlineImages.size() > 0) {
                Set<String> keys = inlineImages.keySet();

                for (String key : keys) {
                    MimeBodyPart inlineImagePart = new MimeBodyPart();
                    inlineImagePart.setDataHandler(inlineImages.get(key));
                    inlineImagePart.setFileName(key);
                    inlineImagePart.setContentID("<" + getContentId(key) + ">");

                    System.out.println("2USING :::: " + getContentId(key));

                    finalMultipart.addBodyPart(inlineImagePart);
                }
            }

            // Send the actual HTML/text message, as big as you like
            message.setContent(finalMultipart);

            if (log.isTraceEnabled()) {
                log.trace("Sending SMTP mail #" + message.getMessageID() + ": " + message.getSubject());
            }

            // Send message
            Transport.send(message);

            if (log.isTraceEnabled()) {
                log.trace("SMTP mail #" + message.getMessageID() + " sent: " + message.getSubject());
            }
        } catch (Throwable t) {
            System.out.println("An error occured attempting to send mail [subject=" + subject + ", to=" + to + ", smtpConfigKey=" + smtpConfigKey + ", protocol=" + protocol + ", host=" + host
                + ", port=" + port + ", from=" + from + ", isAuth="
                + isAuth + ", username=" + username + ", encryption=" + encryption + ", isDebug=" + isDebug + "]");

            log.error(t.getMessage(), t);
            t.printStackTrace();
        }
    }

    protected String getContentId(URL url) {
        if (url == null)
            return null;

        return getContentId(url.getFile());
    }

    protected String getContentId(String name) {
        if (name == null)
            return null;

        if (name.startsWith(Str.SLASH))
            name = name.substring(1);

        name = name.replace(Char.SLASH, Char.MINUS);
        name = name.replace(Char.BACKSLASH, Char.MINUS);

        return Strings.slugify(name);
    }

    private class SMTPAuthenticator extends Authenticator {
        String username = null;
        String password = null;

        private SMTPAuthenticator(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(this.username, this.password);
        }
    }
}
