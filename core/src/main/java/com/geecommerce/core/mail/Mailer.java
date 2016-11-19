package com.geecommerce.core.mail;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

public interface Mailer {
    public void send(String subject, String bodyHtml, String bodyText, String to, String smtpConfigKey, List<URL> attachments, List<URL> inlineImages);

    public void send(String subject, String bodyHtml, String bodyText, String to, String smtpConfigKey, Map<String, byte[]> attachments, Map<String, byte[]> inlineImages);

    public void sendStreams(String subject, String bodyHtml, String bodyText, String to, String smtpConfigKey, Map<String, InputStream> attachments, Map<String, InputStream> inlineImages);
}
