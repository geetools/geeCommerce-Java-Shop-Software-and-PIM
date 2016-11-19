package com.geecommerce.webmessage.service;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.webmessage.model.WebMessage;

import java.util.List;

public interface WebMessageService extends Service {

    public void storeError(String message, String code);

    public void storeInfo(String message, String code);

    public List<WebMessage> fetch(String code);
}
