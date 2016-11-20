package com.geecommerce.core.rest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.geecommerce.core.rest.pojo.Error;
import com.geecommerce.core.service.api.Model;

@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResponseWrapper implements Serializable {
    private static final long serialVersionUID = 127468151964284438L;

    private final Map<String, Object> _metadata;
    private final Map<String, Object> data;
    private final List<Error> errors;

    public ResponseWrapper(Map<String, Object> metadata, Map<String, Object> data, List<Error> errors) {
        this._metadata = metadata;
        this.data = data;
        this.errors = errors;
    }

    public Map<String, Object> get_metadata() {
        return _metadata;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Map<String, Object> metadata = new LinkedHashMap<>();
        private final Map<String, Object> data = new LinkedHashMap<>();
        private final List<Error> errors = new ArrayList<>();
        private String dataKey = null;
        private List<Object> dataList = null;

        public Builder appendMetadata(String key, Object value) {
            metadata.put(key, value);
            return this;
        }

        public Builder appendError(int code, String errorMessage) {
            errors.add(new Error(code, errorMessage));
            return this;
        }

        public Builder begin(String dataKey) {
            this.dataKey = dataKey;
            this.dataList = new ArrayList<>();
            return this;
        }

        public Builder append(Object object) {
            dataList.add(object);
            return this;
        }

        public Builder end() {
            data.put(dataKey, dataList);

            this.dataKey = null;
            this.dataList = null;
            return this;
        }

        public <T extends Model> Builder set(T model) {
            data.put(RestHelper.getName(model), model);
            return this;
        }

        public <T extends Model> Builder set(List<T> models) {
            if (models != null && models.size() > 0) {
                data.put(RestHelper.getPluralName(models), models);
            }

            return this;
        }

        public Builder set(String dataKey, Object dataValue) {
            data.put(dataKey, dataValue);
            return this;
        }

        public ResponseWrapper build() {
            return new ResponseWrapper(metadata, data, errors);
        }
    }
}
