package com.geecommerce.retail.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public interface GeoText extends Model {

    Id getId();

    void setId(Id id);

    Integer getType();

    void setType(Integer type);

    String getValue();

    void setValue(String value);

    class Column {
	public static final String ID = "loc_id";
	public static final String TYPE = "text_type";
	public static final String VALUE = "text_val";
    }
}
