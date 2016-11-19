package com.geecommerce.core.system.pojo;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "item")
public class KeyValue implements Serializable {
    private static final long serialVersionUID = -1628909875056623664L;

    private String key = null;

    private Object value = null;

    public KeyValue() {

    }

    public KeyValue(String key, Object value) {
	this.key = key;
	this.value = value;
    }

    public String getKey() {
	return key;
    }

    public KeyValue setKey(String key) {
	this.key = key;
	return this;
    }

    public Object getValue() {
	return value;
    }

    public KeyValue setValue(Object value) {
	this.value = value;
	return this;
    }
}
