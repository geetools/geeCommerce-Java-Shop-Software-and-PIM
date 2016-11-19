package com.geecommerce.core.proxy;

import com.geecommerce.core.reflect.Reflect;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public class ProxyModel extends AbstractModel {
    private static final long serialVersionUID = 7140727207125084883L;

    private Model m = null;

    public ProxyModel(Model model) {
	this.m = model;
    }

    @Override
    public Id getId() {
	return m.getId();
    }

    public Object get(String field) {
	return Reflect.invokeGetter(m.getClass(), m, field);
    }

    public void set(String field, Object value) {
	Reflect.invokeSetter(m.getClass(), m, field, value);
    }

    public Model getModel() {
	return m;
    }

    @Override
    public String toString() {
	return m.toString();
    }
}
