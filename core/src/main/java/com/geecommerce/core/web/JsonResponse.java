package com.geecommerce.core.web;

import java.util.HashMap;
import java.util.Map;

import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.Json;

import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;

public class JsonResponse {
    private Map<String, Object> params = new HashMap<>();

    public JsonResponse() {
    }

    public JsonResponse(String message) {
        addSuccessMessage(message);
    }

    public JsonResponse(String message, MessageType messageType) {
        switch (messageType) {
        case SUCCESS:
            addSuccessMessage(message);
            break;
        case ERROR:
            addErrorMessage(message);
            break;
        }
    }

    public JsonResponse append(String key, Object value) {
        params.put(key, value);
        return this;
    }

    public JsonResponse appendAll(Map<String, Object> params) {
        this.params.putAll(params);
        return this;
    }

    public JsonResponse addId(Id id) {
        params.put("_id", id.toString());
        return this;
    }

    public JsonResponse addFormUri(String uri) {
        params.put("_formUri", uri);
        return this;
    }

    public JsonResponse addContent(String content) {
        params.put("_content", content);
        return this;
    }

    public JsonResponse addSuccessMessage(String message) {
        params.put("_success_message", message);
        return this;
    }

    public JsonResponse addErrorMessage(String message) {
        params.put("_error_message", message);
        return this;
    }

    public Resolution toResolution() {
        return new StreamingResolution("application/javascript", Json.toJson(params));
    }
}
