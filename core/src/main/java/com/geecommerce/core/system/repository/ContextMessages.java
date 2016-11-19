package com.geecommerce.core.system.repository;

import java.util.List;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.model.ContextMessage;
import com.geecommerce.core.system.model.RequestContext;

public interface ContextMessages extends Repository {
    public ContextMessage havingKey(String key, Merchant merchant);

    public ContextMessage havingKey(String key, Store store);

    public ContextMessage havingKey(String key, RequestContext reqCtx);

    public ContextMessage havingGlobalKey(String key);

    public ContextMessage havingKey(String key);

    public List<ContextMessage> inContext(String key);

    public List<ContextMessage> havingKeysLike(String regex, RequestContext reqCtx);

    public List<ContextMessage> havingKeysLike(String regex, Store store);

    public List<ContextMessage> havingKeysLike(String regex, Merchant merchant);

    public List<ContextMessage> havingGlobalKeysLike(String regex);

    public List<ContextMessage> havingKeysLike(String regex);
}
