package com.geecommerce.core.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Context implements Serializable {
    private static final long serialVersionUID = 3008411204889827252L;

    private Map<String, Object> parameters = new HashMap<>();

    private String message = null;

    private ResponseListener responseListener = null;

    private static final String DEFAULT_KEY = "__default__";

    public Context() {

    }

    public Context(Object value) {
        parameters.put(DEFAULT_KEY, value);
    }

    public Context(String key, Object value) {
        parameters.put(key, value);
    }

    public Context(String key1, Object value1, String key2, Object value2) {
        parameters.put(key1, value1);
        parameters.put(key2, value2);
    }

    public Context(String key1, Object value1, String key2, Object value2, String key3, Object value3) {
        parameters.put(key1, value1);
        parameters.put(key2, value2);
        parameters.put(key3, value3);
    }

    public Context(String key1, Object value1, String key2, Object value2, String key3, Object value3, String key4,
        Object value4) {
        parameters.put(key1, value1);
        parameters.put(key2, value2);
        parameters.put(key3, value3);
        parameters.put(key4, value4);
    }

    public static Context create() {
        return new Context();
    }

    public static Context create(String key, Object value) {
        return new Context(key, value);
    }

    public static Context create(String key1, Object value1, String key2, Object value2) {
        return new Context(key1, value1, key2, value2);
    }

    public static Context create(String key1, Object value1, String key2, Object value2, String key3, Object value3) {
        return new Context(key1, value1, key2, value2, key3, value3);
    }

    public static Context create(String key1, Object value1, String key2, Object value2, String key3, Object value3,
        String key4, Object value4) {
        return new Context(key1, value1, key2, value2, key3, value3, key4, value4);
    }

    public Context append(String key, Object value) {
        parameters.put(key, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) parameters.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T get() {
        return (T) parameters.get(DEFAULT_KEY);
    }

    protected Context setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ResponseListener getResponseListener() {
        return responseListener;
    }

    public Context setResponseListener(ResponseListener responseListener) {
        this.responseListener = responseListener;
        return this;
    }

    public void pushResponse(Object response) {
        if (this.responseListener != null) {
            this.responseListener.onResponse(response);
        }
    }

    @Override
    public String toString() {
        return "Context [parameters=" + parameters + "]";
    }
}
