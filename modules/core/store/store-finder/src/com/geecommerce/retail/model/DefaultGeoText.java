package com.geecommerce.retail.model;

import static com.geecommerce.retail.model.GeoText.Column.ID;
import static com.geecommerce.retail.model.GeoText.Column.TYPE;
import static com.geecommerce.retail.model.GeoText.Column.VALUE;

import java.util.Map;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;
import com.google.common.collect.Maps;

@Model("geodb_textdata")
public class DefaultGeoText extends AbstractModel implements GeoText {

    private static final long serialVersionUID = 133923683264098006L;

    private Id id = null;
    private Integer type;
    private String value;

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void fromMap(Map<String, Object> map) {
        if (map != null) {
            super.fromMap(map);

            this.id = id_(map.get(ID));
            this.type = int_(map.get(TYPE));
            this.value = str_(map.get(VALUE));
        }
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

        map.put(ID, getId());
        map.put(TYPE, getType());
        map.put(VALUE, getValue());

        return map;
    }
}
