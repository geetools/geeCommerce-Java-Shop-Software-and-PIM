package com.geecommerce.core.template.freemarker.directive;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.AttributeSupport;
import com.geecommerce.core.service.ChildSupport;
import com.geecommerce.core.service.ParentSupport;
import com.geecommerce.core.system.attribute.model.AttributeValue;

import freemarker.core.Environment;
import freemarker.ext.beans.BeanModel;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateNumberModel;

public class AttributeHasValueDirective implements TemplateDirectiveModel {
    private static final String CHILD_LOOKUP_NONE = "none";
    private static final String CHILD_LOOKUP_FIRST = "first";
    private static final String CHILD_LOOKUP_ANY = "any";

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
        throws TemplateException, IOException {
        TemplateModel pSource = (TemplateModel) params.get("src");
        SimpleScalar pCode = (SimpleScalar) params.get("code");
        TemplateModel pValue = (TemplateModel) params.get("value");
        TemplateBooleanModel pParent = (TemplateBooleanModel) params.get("parent");
        SimpleScalar pChild = (SimpleScalar) params.get("child");
        SimpleNumber pChildLevels = (SimpleNumber) params.get("child_levels");

        if (pSource == null)
            throw new IllegalArgumentException("The parameter 'source' cannot be null");

        if (pValue == null)
            throw new IllegalArgumentException("The parameter 'value' cannot be null");

        String code = null;
        Object targetValue = null;
        AttributeSupport source = null;
        AttributeValue sourceAttrValue = null;
        boolean fallbackToParent = false;
        String childLookup = CHILD_LOOKUP_NONE;
        Integer childLevels = 1;

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
                sourceAttrValue = (AttributeValue) beanModel;
            } else {
                throw new IllegalArgumentException(
                    "The source-object must be of type AttributeSupportModel or AttributeValue");
            }
        }

        if (pValue instanceof SimpleScalar) {
            targetValue = ((SimpleScalar) pValue).getAsString();
        } else if (pValue instanceof TemplateBooleanModel) {
            targetValue = ((TemplateBooleanModel) pValue).getAsBoolean();
        } else if (pValue instanceof TemplateNumberModel) {
            targetValue = ((TemplateNumberModel) pValue).getAsNumber();
        }

        // Attempt to get value from parent if none could found on current
        // object and parent exists.
        if (pParent != null && source instanceof ParentSupport) {
            fallbackToParent = pParent.getAsBoolean();
        }

        // Attempt to get value from child if none could found on current object
        // or parent.
        if (pChild != null && source instanceof ChildSupport) {
            childLookup = pChild.getAsString();
        }

        if (pChildLevels != null) {
            childLevels = pChildLevels.getAsNumber().intValue();
        }

        boolean hasValue = false;

        try {
            if (sourceAttrValue != null) {
                Object srcValue = sourceAttrValue.getVal();

                if (srcValue != null && srcValue.equals(targetValue))
                    hasValue = true;
            }

            if (!hasValue && source != null) {
                sourceAttrValue = source.getAttribute(code);

                if (sourceAttrValue != null) {
                    Object srcValue = sourceAttrValue.getVal();

                    if (srcValue != null && srcValue.equals(targetValue))
                        hasValue = true;
                }

                // Attempt to get value from parent if it exists. This only
                // works for objects
                // implementing the ParentSupport interface.
                if (!hasValue && fallbackToParent) {
                    AttributeSupport parentSource = (AttributeSupport) ((ParentSupport) source).getParent();

                    if (parentSource != null) {
                        sourceAttrValue = parentSource.getAttribute(code);

                        if (sourceAttrValue != null) {
                            Object srcValue = sourceAttrValue.getVal();

                            if (srcValue != null && srcValue.equals(targetValue))
                                hasValue = true;
                        }
                    }
                }

                // If the parent revealed no result, see if there is anything in
                // the children.
                // This only works for objects implementing the ChildSupport
                // interface.
                if (!hasValue && childLookup != null && !CHILD_LOOKUP_NONE.equals(childLookup)) {
                    List<AttributeSupport> children = ((ChildSupport) source).getAnyChildren();

                    if (children != null && children.size() > 0) {
                        if (CHILD_LOOKUP_FIRST.equals(childLookup)) {
                            AttributeSupport childSource = children.get(0);
                            sourceAttrValue = childSource.getAttribute(code);

                            if (sourceAttrValue != null) {
                                Object srcValue = sourceAttrValue.getVal();

                                if (srcValue != null && srcValue.equals(targetValue))
                                    hasValue = true;
                            }
                        } else if (CHILD_LOOKUP_ANY.equals(childLookup)) {
                            if (children != null && !children.isEmpty()) {
                                for (AttributeSupport childSource : children) {
                                    if (!((ChildSupport) childSource).isValidChild())
                                        continue;

                                    sourceAttrValue = childSource.getAttribute(code);

                                    if (sourceAttrValue != null) {
                                        Object srcValue = sourceAttrValue.getVal();

                                        if (srcValue != null && srcValue.equals(targetValue)) {
                                            hasValue = true;
                                            break;
                                        }
                                    }
                                }

                                // Attempt the next level if no value could be
                                // found.
                                if (!hasValue && childLevels != null && childLevels > 1) {
                                    for (AttributeSupport childL1Source : children) {
                                        if (childL1Source instanceof ChildSupport) {
                                            if (!((ChildSupport) childL1Source).isValidChild())
                                                continue;

                                            List<AttributeSupport> childrenL2 = ((ChildSupport) childL1Source)
                                                .getAnyChildren();

                                            if (childrenL2 == null || childrenL2.size() == 0)
                                                continue;

                                            for (AttributeSupport childSourceL2 : childrenL2) {
                                                sourceAttrValue = childSourceL2.getAttribute(code);

                                                if (sourceAttrValue != null) {
                                                    Object srcValue = sourceAttrValue.getVal();

                                                    if (srcValue != null && srcValue.equals(targetValue)) {
                                                        hasValue = true;
                                                        break;
                                                    }
                                                }
                                            }
                                        }

                                        if (hasValue)
                                            break;
                                    }
                                }
                            }
                        }
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

        if (hasValue && body != null) {
            body.render(env.getOut());
        }
    }
}
