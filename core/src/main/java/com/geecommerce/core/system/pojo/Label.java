package com.geecommerce.core.system.pojo;

import java.io.Serializable;

import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public class Label implements Serializable {
    private static final long serialVersionUID = 389220216883911544L;

    private Id id = null;
    private ContextObject<String> label = null;

    public Label() {

    }

    public Label(Id id, ContextObject<String> label) {
	this.id = id;
	this.label = label;
    }

    public Id getId() {
	return id;
    }

    public Label setId(Id id) {
	this.id = id;
	return this;
    }

    public ContextObject<String> getLabel() {
	return label;
    }

    public Label setLabel(ContextObject<String> label) {
	this.label = label;
	return this;
    }
}
