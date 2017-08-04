package com.geecommerce.core.template.freemarker.directive;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.enums.FrontendOutput;
import com.geecommerce.core.service.AttributeSupport;
import com.geecommerce.core.service.ChildSupport;
import com.geecommerce.core.service.ParentSupport;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeOption;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.type.Id;

import freemarker.core.Environment;
import freemarker.ext.beans.BeanModel;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateNumberModel;
import freemarker.template.utility.DeepUnwrap;

public class AttributeDirective implements TemplateDirectiveModel {
    private static final String CHILD_LOOKUP_NONE = "none";
    private static final String CHILD_LOOKUP_FIRST = "first";
    private static final String CHILD_LOOKUP_ANY = "any";

    private static final String FORMAT_TYPE_CURRENCY = "currency";
    private static final String FORMAT_TYPE_MEASUREMENT = "measurement";
    private static final String FORMAT_TYPE_LOCALIZED_BOOLEAN = "localized_boolean";
    private static final String FORMAT_TYPE_PLAIN_TEXT = "plain-text";
    private static final String FORMAT_TYPE_SHORT_TEXT = "short-text";

    private static final String FROMAT_KEY = "attributes/measurement/format";
    private static final String UNIT_KEY = "attributes/measurement/unit";

    private static final Pattern brPattern = Pattern.compile("<br[ \\/]{0,2}>");

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
        throws TemplateException, IOException {
        TemplateModel pSource = (TemplateModel) params.get("src");
        SimpleScalar pCode = (SimpleScalar) params.get("code");
        SimpleScalar pPrint = (SimpleScalar) params.get("print");
        TemplateBooleanModel pParent = (TemplateBooleanModel) params.get("parent");
        SimpleScalar pChild = (SimpleScalar) params.get("child");
        SimpleNumber pChildLevels = (SimpleNumber) params.get("child_levels");

        TemplateModel pDefault = (TemplateModel) params.get("default");
        SimpleScalar pVar = (SimpleScalar) params.get("var");
        SimpleScalar pMake = (SimpleScalar) params.get("make");
        SimpleScalar pFormat = (SimpleScalar) params.get("format");
        SimpleScalar pMessage = (SimpleScalar) params.get("message");
        TemplateBooleanModel pStripTags = (TemplateBooleanModel) params.get("strip_tags");
        TemplateBooleanModel pUpperCase = (TemplateBooleanModel) params.get("uppercase");
        TemplateBooleanModel pStripNewlines = (TemplateBooleanModel) params.get("strip_newlines");
        SimpleScalar pReplaceNewlines = (SimpleScalar) params.get("replace_newlines");
        TemplateNumberModel pTruncate = (TemplateNumberModel) params.get("truncate");

        TemplateNumberModel pListMaxRows = (TemplateNumberModel) params.get("list_max_rows");
        TemplateNumberModel pListMaxRowLength = (TemplateNumberModel) params.get("list_max_row_length");

        if (pSource == null)
            throw new IllegalArgumentException("The parameter 'source' cannot be null");

        String code = null;
        AttributeSupport source = null;
        AttributeValue value = null;
        String print = null;
        String varName = null;
        String format = null;
        String make = null;
        String message = null;
        boolean fallbackToParent = false;
        String childLookup = CHILD_LOOKUP_NONE;
        Integer childLevels = 1;
        boolean stripTags = false;
        boolean upperCase = false;
        boolean stripNewlines = false;
        String replaceNewlines = null;
        Number truncateAt = null;
        Number listMaxRows = null;
        Number listMaxRowLength = null;

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

        if (pPrint != null) {
            print = pPrint.getAsString().toLowerCase().trim();

            if (!"label".equals(print) && !"value".equalsIgnoreCase(print.trim())) {
                throw new IllegalArgumentException("The print attribute can only be one of ['label', 'value']");
            }
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

        // Optionally put the result into a parameters map instead of outputting
        // it.
        if (pVar != null)
            varName = pVar.getAsString();

        // Show value as (list)
        if (pMake != null)
            make = pMake.getAsString();

        // Optionally format the string.
        if (pFormat != null)
            format = pFormat.getAsString();

        if (pReplaceNewlines != null)
            replaceNewlines = pReplaceNewlines.getAsString();

        // Optionally strip HTML tags
        if (pStripTags != null)
            stripTags = pStripTags.getAsBoolean();

        // Optionally uppercase
        if (pUpperCase != null)
            upperCase = pUpperCase.getAsBoolean();

        // Optionally strip newlines
        if (pStripNewlines != null)
            stripNewlines = pStripNewlines.getAsBoolean();

        // Optionally truncate string
        if (pTruncate != null)
            truncateAt = pTruncate.getAsNumber();

        // Optionally extract list with max number of rows
        if (pListMaxRows != null)
            listMaxRows = pListMaxRows.getAsNumber();

        // Optionally extract list with max row length
        if (pListMaxRowLength != null)
            listMaxRowLength = pListMaxRowLength.getAsNumber();

        // Optionally use context-message from DB.
        if (pMessage != null)
            message = pMessage.getAsString();

        Object result = null;

        try {
            if (source != null && value == null) {
                value = source.getAttribute(code);

                // Attempt to get value from parent if it exists.This only works
                // for objects implementing the
                // ParentSupport interface.
                if ((value == null || value.getValue() == null || StringUtils.isBlank(value.getValue().str()))
                    && fallbackToParent) {
                    AttributeSupport parentSource = (AttributeSupport) ((ParentSupport) source).getParent();

                    if (parentSource != null)
                        value = parentSource.getAttribute(code);
                }

                // If the parent revealed no result, see if there is anything in
                // the children.
                // This only works for objects implementing the ChildSupport
                // interface.
                if (value == null && childLookup != null && !CHILD_LOOKUP_NONE.equals(childLookup)) {
                    List<AttributeSupport> children = ((ChildSupport) source).getAnyChildren();

                    if (children != null && children.size() > 0) {
                        if (CHILD_LOOKUP_FIRST.equals(childLookup)) {
                            AttributeSupport childSource = children.get(0);
                            value = childSource.getAttribute(code);
                        } else if (CHILD_LOOKUP_ANY.equals(childLookup)) {
                            if (children != null && children.size() != 0) {
                                for (AttributeSupport childSource : children) {
                                    value = childSource.getAttribute(code);

                                    if (value != null)
                                        break;
                                }

                                // Attempt the next level if no value could be
                                // found.
                                if (value == null && childLevels != null && childLevels > 1) {
                                    for (AttributeSupport childL1Source : children) {
                                        if (childL1Source instanceof ChildSupport) {
                                            List<AttributeSupport> childrenL2 = ((ChildSupport) childL1Source)
                                                .getAnyChildren();

                                            if (childrenL2 == null || childrenL2.size() == 0)
                                                continue;

                                            for (AttributeSupport childSourceL2 : childrenL2) {
                                                value = childSourceL2.getAttribute(code);

                                                if (value != null)
                                                    break;
                                            }
                                        }

                                        if (value != null)
                                            break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (value != null) {
                // value case
                if (print == null || "value".equals(print)) {
                    if (value.getAttributeOptions() != null && value.getAttributeOptions().size() > 0) {
                        Map<Id, AttributeOption> attributeOptions = value.getAttributeOptions();
                        List<String> labels = new LinkedList<>();
                        for (Id key : attributeOptions.keySet()) {
                            AttributeOption option = attributeOptions.get(key);
                            if (option.getLabel() != null && !StringUtils.isBlank(option.getLabel().getString()))
                                labels.add(option.getLabel().getString());
                        }

                        if (make != null && "list".equals(make)
                            || value.getAttribute().getFrontendOutput().equals(FrontendOutput.LIST)) {

                            StringBuilder asList = new StringBuilder();
                            asList.append("<ul class=\"list-attr-" + code + "\">");

                            for (String label : labels) {
                                asList.append("<li><span>").append(label).append("</span></li>");
                            }

                            asList.append("</ul>");

                            result = asList.toString();
                        } else {
                            result = StringUtils.join(labels, ", ");
                        }
                    } else {
                        result = value.getVal();
                    }
                }

                // value case

                // label case
                if (print != null && "label".equals(print)) {
                    Attribute attr = value.getAttribute();

                    if (attr != null) {
                        result = attr.getFrontendLabel().getString();
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

            // throw new IllegalArgumentException(t.getMessage(), t);
        }

        if (result == null && pDefault != null)
            result = DeepUnwrap.unwrap(pDefault);

        if (result != null) {
            if (format == null && value != null) {
                Attribute attr = value.getAttribute();

                if (attr != null && attr.getFrontendFormat() != null
                    && !StringUtils.isBlank(attr.getFrontendFormat().str()))
                    format = attr.getFrontendFormat().str().toLowerCase();
            }

            if (format != null) {
                result = format(result, format);
            }

            if (stripTags) {
                result = com.geecommerce.core.util.Strings.stripTags(result.toString());
            }

            if (upperCase) {
                result = result.toString().toUpperCase();
            }

            if (stripNewlines) {
                result = com.geecommerce.core.util.Strings.stripNewlines(result.toString());
            }

            if (replaceNewlines != null && !"".equals(replaceNewlines.trim())) {
                result = com.geecommerce.core.util.Strings.replaceNewlines(result.toString(), replaceNewlines);
            }

            if (truncateAt != null) {
                result = com.geecommerce.core.util.Strings.truncateNicely(result.toString(), truncateAt.intValue(),
                    "...");
            }

            if (listMaxRows != null) {
                try {
                    Document doc = Jsoup.parse(result.toString());

                    Elements elements = doc.getElementsByTag("li");

                    StringBuilder html = new StringBuilder("<ul>\n");

                    int rowNum = 0;

                    for (Element el : elements) {
                        String liText = el.text();

                        if (listMaxRowLength != null) {
                            liText = com.geecommerce.core.util.Strings.truncateNicely(liText,
                                listMaxRowLength.intValue(), "...");
                        }

                        html.append("<li>").append(liText).append("</li>");

                        if (rowNum >= listMaxRows.intValue() - 1)
                            break;

                        rowNum++;
                    }

                    html.append("</ul>");

                    result = html.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (message != null) {
                result = message(env, result, message);
            }

            if (varName != null) {
                // Sets the result into the current template as if using
                // <#assign name=model>.
                env.setVariable(varName, DefaultObjectWrapper.getDefaultInstance().wrap(result));
            } else {
                // Simply writes the result to the template.
                env.getOut().write(result.toString());
            }
        }
    }

    private String message(Environment env, Object result, String message) throws TemplateException, IOException {
        if (result == null && message == null)
            return null;

        if (result == null)
            return message;

        if (message == null)
            return result.toString();

        Map<String, Object> params = new HashMap<>();
        params.put("text", new SimpleScalar(message));
        params.put("param1", new SimpleScalar(result == null ? "" : result.toString()));

        String varKey = new StringBuilder("cb-pd-").append(message.hashCode()).toString();
        params.put("var", new SimpleScalar(varKey));

        MessageDirective md = new MessageDirective();
        md.execute(env, params, null, null);

        SimpleScalar pVal = (SimpleScalar) env.getVariable(varKey);

        return pVal == null ? message : pVal.getAsString();
    }

    private String format(Object value, String format) {
        ApplicationContext appCtx = App.get().context();

        Locale locale = null;

        if (appCtx != null) {
            RequestContext reqCtx = appCtx.getRequestContext();
            locale = new Locale(reqCtx.getLanguage(), reqCtx.getCountry());
        }

        if (FORMAT_TYPE_CURRENCY.equalsIgnoreCase(format)) {
            return NumberFormat.getCurrencyInstance(locale).format(Double.valueOf(value.toString()));
        } else if (FORMAT_TYPE_MEASUREMENT.equalsIgnoreCase(format)) {
            return formatMeasurement(value);
        } else if (FORMAT_TYPE_LOCALIZED_BOOLEAN.equalsIgnoreCase(format)) {
            return App.get().message("attr.value." + value.toString());
        } else if (FORMAT_TYPE_PLAIN_TEXT.equalsIgnoreCase(format)) {
            return com.geecommerce.core.util.Strings.stripTags(value.toString());
        } else if (FORMAT_TYPE_SHORT_TEXT.equalsIgnoreCase(format)) {
            String plainText = value.toString();

            plainText = plainText.replace("</li>", "\n");

            Matcher m = brPattern.matcher(plainText);

            if (m.find())
                plainText = m.replaceAll("\n");

            plainText = plainText.replace("\r", "");
            plainText = plainText.replace("\n\n", "\n");
            plainText = com.geecommerce.core.util.Strings.stripTags(plainText);
            plainText = com.geecommerce.core.util.Strings.replaceNewlines(plainText, ", ");
            plainText = plainText.replace(", ,", ",");

            return com.geecommerce.core.util.Strings.truncateNicely(plainText, 150, "...");
        } else {
            return String.format(locale, format, value);
        }
    }

    private String formatMeasurement(Object value) {
        String format = App.get().cpStr_(FROMAT_KEY, "%s %s");
        String unit = App.get().cpStr_(UNIT_KEY, "cm");
        return String.format("%s %s", value.toString(), "cm");
    }
}
