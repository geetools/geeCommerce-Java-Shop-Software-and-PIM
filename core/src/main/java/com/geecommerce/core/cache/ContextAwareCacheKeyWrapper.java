package com.geecommerce.core.cache;

import java.io.Serializable;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.type.Id;

public class ContextAwareCacheKeyWrapper<T> implements Serializable, Comparable<ContextAwareCacheKeyWrapper<T>> {
    private static final long serialVersionUID = 7328777097688357786L;

    private Id reqCtxId = null;
    private T key = null;

    public ContextAwareCacheKeyWrapper(T key) {
        this.key = key;

        ApplicationContext appCtx = App.get().getApplicationContext();

        if (appCtx != null && appCtx.getRequestContext() != null) {
            RequestContext reqCtx = appCtx.getRequestContext();
            reqCtxId = reqCtx.getId();
        } else {
            reqCtxId = Id.valueOf(0);
        }
    }

    public Id getReqCtxId() {
        return reqCtxId;
    }

    public T getKey() {
        return key;
    }

    public boolean isInContext() {
        ApplicationContext appCtx = App.get().getApplicationContext();

        if (appCtx != null && appCtx.getRequestContext() != null) {
            RequestContext reqCtx = appCtx.getRequestContext();
            return reqCtx.getId().equals(reqCtxId);
        } else {
            return true;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result + ((reqCtxId == null) ? 0 : reqCtxId.hashCode());
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        ContextAwareCacheKeyWrapper<T> other = (ContextAwareCacheKeyWrapper<T>) obj;

        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;

        if (reqCtxId == null) {
            if (other.reqCtxId != null)
                return false;
        } else if (!reqCtxId.equals(other.reqCtxId))
            return false;

        return true;
    }

    @Override
    public int compareTo(ContextAwareCacheKeyWrapper<T> o) {
        return this.toString().compareTo(o.toString());
    }

    @Override
    public String toString() {
        return "ContextAwareCacheKeyWrapper [reqCtxId=" + reqCtxId + ", key=" + key + "]";
    }
}
