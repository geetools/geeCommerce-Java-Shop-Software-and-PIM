package com.geecommerce.webmessage.service;

import java.util.ArrayList;
import java.util.List;

import com.geecommerce.core.App;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.webmessage.model.WebMessage;
import com.google.inject.Inject;

@Service
public class DefaultWebMessageService implements WebMessageService {
    @Inject
    protected App app;

    protected static String SESSION_KEY = "WEB_MESSAGES";

    protected void storeMessage(String status, String message, String code) {
        List<WebMessage> messages = app.sessionGet(SESSION_KEY);
        if (messages == null) {
            messages = new ArrayList<>();
        }
        WebMessage webMessage = app.getModel(WebMessage.class);
        webMessage.setStatus(status).setCode(code).setMessage(message);
        messages.add(webMessage);
        app.sessionSet(SESSION_KEY, messages);
    }

    @Override
    public void storeError(String message, String code) {
        storeMessage("ERROR", message, code);
    }

    @Override
    public void storeInfo(String message, String code) {
        storeMessage("INFO", message, code);
    }

    @Override
    public List<WebMessage> fetch(String code) {
        List<WebMessage> messages = app.sessionGet(SESSION_KEY);
        if (messages == null) {
            return new ArrayList<>();
        }
        List<WebMessage> result = new ArrayList<>();
        for (WebMessage webMessage : messages) {
            if (webMessage.getCode().equals(code)) {
                result.add(webMessage);
            }
        }
        for (WebMessage webMessage : result) {
            messages.remove(webMessage);
        }
        app.sessionSet(SESSION_KEY, messages);
        return result;
    }
}
