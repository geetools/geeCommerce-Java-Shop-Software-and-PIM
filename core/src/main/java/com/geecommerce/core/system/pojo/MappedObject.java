package com.geecommerce.core.system.pojo;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "item")
public class MappedObject implements Serializable {
    private static final long serialVersionUID = -8684587167650279622L;

    private Map<String, Object> map = new LinkedHashMap<String, Object>();

    public MappedObject() {

    }

    public MappedObject(String key, Object value) {
	map.put(key, value);
    }

    public MappedObject(String key1, Object value1, String key2, Object value2) {
	map.put(key1, value1);
	map.put(key2, value2);
    }

    public MappedObject(String key1, Object value1, String key2, Object value2, String key3, Object value3) {
	map.put(key1, value1);
	map.put(key2, value2);
	map.put(key3, value3);
    }

    public MappedObject append(String key, Object value) {
	map.put(key, value);
	return this;
    }
}
