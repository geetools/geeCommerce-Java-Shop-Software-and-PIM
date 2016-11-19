package com.geecommerce.core.system.service;

import java.util.List;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.model.ContextMessage;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface ContextMessageService extends Service {
    public ContextMessage storeMessage(String key, ContextObject<String> message, RequestContext reqCtx);

    public ContextMessage storeMessage(String key, ContextObject<String> message, Store store);

    public ContextMessage storeMessage(String key, ContextObject<String> message, Merchant merchant);

    public ContextMessage storeMessage(String key, ContextObject<String> message);

    public void update(ContextMessage contextMessage);

    public void remove(ContextMessage contextMessage);

    public ContextMessage getMessage(Id id);

    public ContextMessage findMessage(String key, Merchant merchant);

    public ContextMessage findMessage(String key, Store store);

    public ContextMessage findMessage(String key, RequestContext reqCtx);

    public ContextMessage findGlobalMessage(String key);

    public ContextMessage findMessage(String key);

    public List<ContextMessage> findMessages(String regex, Merchant merchant);

    public List<ContextMessage> findMessages(String regex, Store store);

    public List<ContextMessage> findMessages(String regex, RequestContext reqCtx);

    public List<ContextMessage> findGlobalMessages(String regex);

    public List<ContextMessage> findMessages(String regex);

    public List<ContextMessage> findAllMessagesInContext(String key);

    public ContextMessage getOrSetMessage(String key, ContextObject<String> message);
}
