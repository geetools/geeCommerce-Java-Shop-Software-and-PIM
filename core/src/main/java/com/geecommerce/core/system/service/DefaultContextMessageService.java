package com.geecommerce.core.system.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.model.ContextMessage;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.system.repository.ContextMessages;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;

public class DefaultContextMessageService implements ContextMessageService {
    @Inject
    protected App app;

    protected final ContextMessages ctxMessages;

    @Inject
    public DefaultContextMessageService(ContextMessages ctxMessages) {
        this.ctxMessages = ctxMessages;
    }

    @Override
    public ContextMessage storeMessage(String key, ContextObject<String> message, RequestContext reqCtx) {

        if (key == null || reqCtx == null)
            throw new NullPointerException("Unable to insert ContextMessage: [key=" + key + ", reqCtx=" + reqCtx + "]");

        ContextMessage cm = findMessage(key, reqCtx);

        if (cm == null) {
            cm = app.model(ContextMessage.class);
            cm.setRequestContextId(reqCtx.getId());
            cm.setKey(key).setValue(message);

            ctxMessages.add(cm);
        } else {
            cm.setValue(message);
            ctxMessages.update(cm);
        }

        return cm;
    }

    @Override
    public ContextMessage storeMessage(String key, ContextObject<String> message, Store store) {
        if (key == null || store == null)
            throw new NullPointerException("Unable to insert ContextMessage: [key=" + key + ", store=" + store + "]");

        ContextMessage cm = findMessage(key, store);

        if (cm == null) {
            cm = app.model(ContextMessage.class);
            cm.setStoreId(store.getId());
            cm.setKey(key).setValue(message);

            ctxMessages.add(cm);
        } else {
            cm.setValue(message);
            ctxMessages.update(cm);
        }

        return cm;
    }

    @Override
    public ContextMessage storeMessage(String key, ContextObject<String> message, Merchant merchant) {
        if (key == null || merchant == null)
            throw new NullPointerException(
                "Unable to insert ContextMessage: [key=" + key + ", merchant=" + merchant + "]");

        ContextMessage cm = findMessage(key, merchant);

        if (cm == null) {
            cm = app.model(ContextMessage.class);
            cm.setMerchantId(merchant.getId());
            cm.setKey(key).setValue(message);

            ctxMessages.add(cm);
        } else {
            cm.setValue(message);
            ctxMessages.update(cm);
        }

        return cm;
    }

    @Override
    public ContextMessage storeMessage(String key, ContextObject<String> message) {
        if (key == null || message == null)
            throw new NullPointerException(
                "Unable to insert ContextMessage: [key=" + key + ", message=" + message + "]");

        ContextMessage cm = findGlobalMessage(key);

        if (cm == null) {
            cm = app.model(ContextMessage.class);
            cm.setKey(key).setValue(message);

            ctxMessages.add(cm);
        } else {
            cm.setValue(message);
            ctxMessages.update(cm);
        }

        return cm;
    }

    @Override
    public void update(ContextMessage contextMessage) {
        ctxMessages.update(contextMessage);
    }

    @Override
    public void remove(ContextMessage contextMessage) {
        ctxMessages.remove(contextMessage);
    }

    @Override
    public ContextMessage getMessage(Id id) {
        return ctxMessages.findById(ContextMessage.class, id);
    }

    @Override
    public ContextMessage findMessage(String key, RequestContext reqCtx) {
        return ctxMessages.havingKey(key, reqCtx);
    }

    @Override
    public ContextMessage findMessage(String key, Store store) {
        return ctxMessages.havingKey(key, store);
    }

    @Override
    public ContextMessage findMessage(String key, Merchant merchant) {
        return ctxMessages.havingKey(key, merchant);
    }

    @Override
    public ContextMessage findGlobalMessage(String key) {
        return ctxMessages.havingGlobalKey(key);
    }

    @Override
    public ContextMessage findMessage(String key) {
        return ctxMessages.havingKey(key);
    }

    @Override
    public List<ContextMessage> findAllMessagesInContext(String key) {
        return ctxMessages.inContext(key);
    }

    @Override
    public List<ContextMessage> findMessages(String regex, RequestContext reqCtx) {
        return ctxMessages.havingKeysLike(regex, reqCtx);
    }

    @Override
    public List<ContextMessage> findMessages(String regex, Store store) {
        return ctxMessages.havingKeysLike(regex, store);
    }

    @Override
    public List<ContextMessage> findMessages(String regex, Merchant merchant) {
        return ctxMessages.havingKeysLike(regex, merchant);
    }

    @Override
    public List<ContextMessage> findGlobalMessages(String regex) {
        return ctxMessages.havingGlobalKeysLike(regex);
    }

    @Override
    public List<ContextMessage> findMessages(String regex) {
        return ctxMessages.havingKeysLike(regex);
    }

    @Override
    public ContextMessage getOrSetMessage(String key, ContextObject<String> message) {
        // Attempt to get existing message already stored in DB.
        ContextMessage existingMessage = findMessage(key);

        // If it already exists, see if any new context (i.e. language) value
        // has been added.
        if (existingMessage != null) {
            ContextObject<String> existingCtxMessages = existingMessage.getValue();
            boolean containsNewMessages = false;

            for (Map<String, Object> ctxMessage : message) {
                // We are not looking for a change in value, but only to see if
                // a new context has been added.
                Map<String, Object> contextMap = new HashMap<>(ctxMessage);
                contextMap.remove(ContextObject.VALUE);

                boolean messageforContextExsts = false;

                for (Map<String, Object> existingCtxMessage : existingCtxMessages) {
                    Map<String, Object> existingContextMap = new HashMap<>(existingCtxMessage);
                    existingContextMap.remove(ContextObject.VALUE);

                    // See if a new context value has been added to the context
                    // object.
                    if (existingContextMap.equals(contextMap)) {
                        messageforContextExsts = true;
                        break;
                    }
                }

                // If there is a new context value we add it to t he existing
                // one for updating in DB.
                if (!messageforContextExsts) {
                    existingCtxMessages.add(ctxMessage);
                    containsNewMessages = true;
                }
            }

            // Update in DB if necessary.
            if (containsNewMessages)
                ctxMessages.update(existingMessage);

            return existingMessage;
        }
        // If the ContextMessage does not exist at all yet, just create and
        // return.
        else {
            return storeMessage(key, message);
        }
    }
}
