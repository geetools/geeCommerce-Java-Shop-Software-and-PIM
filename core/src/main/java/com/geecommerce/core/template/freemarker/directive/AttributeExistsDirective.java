package com.geecommerce.core.template.freemarker.directive;

import java.io.IOException;
import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.AttributeSupport;
import com.geecommerce.core.service.ParentSupport;
import com.geecommerce.core.system.attribute.model.AttributeValue;

import freemarker.core.Environment;
import freemarker.ext.beans.BeanModel;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class AttributeExistsDirective implements TemplateDirectiveModel {
    @SuppressWarnings("rawtypes")
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
        throws TemplateException, IOException {
        TemplateModel pSource = (TemplateModel) params.get("src");
        SimpleScalar pCode = (SimpleScalar) params.get("code");
        TemplateBooleanModel pParent = (TemplateBooleanModel) params.get("parent");

        if (pSource == null)
            throw new IllegalArgumentException("The parameter 'source' cannot be null");

        String code = null;
        AttributeSupport source = null;
        AttributeValue value = null;
        boolean fallbackToParent = false;

        if (pSource instanceof BeanModel) {
            Object beanModel = ((BeanModel) pSource).getWrappedObject();

            if (beanModel instanceof AttributeSupport) {
                source = (AttributeSupport) beanModel;

                if (pCode == null) {
                    throw new IllegalArgumentException(
                        "The parameter 'code' cannot be null if source type is not an AttributeValue object");
                } else {
                    code = pCode.getAsString();
                }
            } else if (beanModel instanceof AttributeValue) {
                value = (AttributeValue) beanModel;
            } else {
                throw new IllegalArgumentException(
                    "The source-object must be of type AttributeSupportModel or AttributeValue");
            }
        }

        // Attempt to get value from parent if none could found on current
        // object and parent exists.
        if (pParent != null && source instanceof ParentSupport) {
            fallbackToParent = pParent.getAsBoolean();
        }

        Object result = null;

        try {
            if (source != null && value == null) {
                value = source.getAttribute(code);

                // Attempt to get value from parent if it exists. This only
                // works for objects
                // implementing the ParentSupport interface.
                if (value == null && fallbackToParent) {
                    AttributeSupport parentSource = (AttributeSupport) ((ParentSupport) source).getParent();

                    if (parentSource != null)
                        value = parentSource.getAttribute(code);
                }
            }

            if (value != null) {
                result = value.firstLabel();

                if (result == null) {
                    if (value.allLabels() != null && value.allLabels().size() > 0) {
                        result = value.allLabels().get(0);
                    }
                }
            }
        } catch (Throwable t) {
            // Make sure that the error message is not swallowed somewhere
            // unnoticed
            // in dev-mode. This way it is clearly visible in the console.
            if (App.get().isDevPrintErrorMessages()) {
                t.printStackTrace();
            }

            throw new IllegalArgumentException(t.getMessage(), t);
        }

        if (result != null && body != null) {
            body.render(env.getOut());
        }
    }
}
