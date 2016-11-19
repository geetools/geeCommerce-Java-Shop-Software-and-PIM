package com.geecommerce.core.service.api;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import com.geecommerce.core.event.Observable;
import com.geecommerce.core.type.HistorySupport;
import com.geecommerce.core.type.IdSupport;
import com.geecommerce.core.type.Versionable;

public interface Model extends IdSupport, HistorySupport, Versionable, Observable, Serializable {
    public Model setModifiedBy(String modifiedBy);

    public String getModifiedBy();

    public Model setModifiedOn(Date modifiedOn);

    public Date getModifiedOn();

    public Model setCreatedBy(String createdBy);

    public String getCreatedBy();

    public Model setCreatedOn(Date createdOn);

    public Date getCreatedOn();

    public void fromMap(Map<String, Object> map);

    public Map<String, Object> toMap();

    public void set(Map<String, Object> updateMap);

    public void set(Map<String, Object> updateMap, boolean override);

    public void set(String field, Object value);
}
