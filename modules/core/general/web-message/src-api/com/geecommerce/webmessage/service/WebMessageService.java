package com.geecommerce.webmessage.service;

import java.util.List;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.webmessage.model.WebMessage;

public interface WebMessageService extends Service {

    public void storeError(String message, String code);

    public void storeInfo(String message, String code);

    public List<WebMessage> fetch(String code);
}
