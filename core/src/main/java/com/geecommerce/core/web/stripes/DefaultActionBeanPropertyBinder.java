package com.geecommerce.core.web.stripes;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.validation.ValidationErrors;

public class DefaultActionBeanPropertyBinder extends net.sourceforge.stripes.controller.DefaultActionBeanPropertyBinder {

    @Override
    public ValidationErrors bind(ActionBean bean, ActionBeanContext context, boolean validate) {
	return super.bind(bean, context, validate);
    }

    @Override
    public void bind(ActionBean bean, String propertyName, Object propertyValue) throws Exception {
	super.bind(bean, propertyName, propertyValue);
    }

}
