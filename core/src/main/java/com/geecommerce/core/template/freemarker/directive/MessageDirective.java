package com.geecommerce.core.template.freemarker.directive;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.boon.Str;

import com.geecommerce.core.App;
import com.geecommerce.core.system.model.ContextMessage;

import freemarker.core.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class MessageDirective implements TemplateDirectiveModel {
    @SuppressWarnings("rawtypes")
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
        SimpleScalar pVar = (SimpleScalar) params.get("var");
        SimpleScalar pLang = (SimpleScalar) params.get("lang");
        TemplateBooleanModel pEditable = (TemplateBooleanModel) params.get("editable");

        // Optionally store an alternative translation.
        SimpleScalar pLang2 = (SimpleScalar) params.get("lang2");
        SimpleScalar pMessage2 = (SimpleScalar) params.get("text2");

        // Optionally store another alternative translation.
        SimpleScalar pLang3 = (SimpleScalar) params.get("lang3");
        SimpleScalar pMessage3 = (SimpleScalar) params.get("text3");

        String message = null;
        String varName = null;
        String language = null;
        String language2 = null;
        String message2 = null;
        String language3 = null;
        String message3 = null;
        boolean isEditable = true;

        App app = App.get();

        // Optionally put the result into a parameters map instead of outputting
        // it.
        if (pVar != null)
            varName = pVar.getAsString();

        if (pLang != null)
            language = pLang.getAsString();

        if (pLang2 != null)
            language2 = pLang2.getAsString();

        if (pMessage2 != null)
            message2 = pMessage2.getAsString();

        if (pLang3 != null)
            language3 = pLang3.getAsString();

        if (pMessage3 != null)
            message3 = pMessage3.getAsString();

        // --------------------------------------------------------
        // Attempt to get context message from directive body.
        // --------------------------------------------------------

        if (body != null) {
            StringWriter sw = new StringWriter();

            try {
                body.render(sw);
                String bodyMsg = sw.toString();

                if (!Str.isEmpty(bodyMsg)) {
                    message = bodyMsg;
                }
            } finally {
                IOUtils.closeQuietly(sw);
            }
        }

        // --------------------------------------------------------
        // Attempt to get context message from directive attribute.
        // --------------------------------------------------------

        if (message == null && params.get("text") != null) {
            SimpleScalar param = (SimpleScalar) params.get("text");
            String paramMsg = param.getAsString();

            if (!Str.isEmpty(paramMsg)) {
                message = paramMsg;
            }
        }

        if (message != null) {
            ContextMessage contextMessage = app.contextMessage(message, language, message2, language2, message3, language3);
            String ctxMsg = contextMessage.getMessage();

            if (ctxMsg != null) {
                ctxMsg = ctxMsg.replaceAll("\n", "<br/>");

                // Add parameters if they exist.
                SimpleScalar p1 = (SimpleScalar) params.get("param1");
                SimpleScalar p2 = (SimpleScalar) params.get("param2");
                SimpleScalar p3 = (SimpleScalar) params.get("param3");

                List<Object> paramsList = new ArrayList<>();

                if (p1 != null)
                    paramsList.add(p1.getAsString());

                if (p2 != null)
                    paramsList.add(p2.getAsString());

                if (p3 != null)
                    paramsList.add(p3.getAsString());

                if (paramsList.size() > 0) {
                    ctxMsg = String.format(ctxMsg, paramsList.toArray());
                }
            }

            if (app.isDevToolbar()) {
                ctxMsg = "|id=" + contextMessage.getId() + "|" + ctxMsg + "|";
            } else {
                if (pEditable != null)
                    isEditable = pEditable.getAsBoolean();
            }

            boolean editHeaderExists = app.editHeaderExists();
            boolean editAllowed = app.editAllowed();

            if (varName != null) {
                // Sets the result into the current template as if using
                // <#assign name=model>.
                env.setVariable(varName, new SimpleScalar(ctxMsg));
            } else {
                if (isEditable && editHeaderExists && editAllowed) {
                    // Simply writes the result to the template.
                    env.getOut().write(
                        new StringBuilder("<div contenteditable=\"true\" class=\"cb-editable\" id=\"cb-editable-").append(contextMessage.getId()).append("\" data-id=\"").append(contextMessage.getId())
                            .append("\" data-modified=\"0\">")
                            .append(ctxMsg).append("</div>").toString());
                } else {
                    // Simply writes the result to the template.
                    env.getOut().write(ctxMsg);
                }
            }
        }
    }
}
