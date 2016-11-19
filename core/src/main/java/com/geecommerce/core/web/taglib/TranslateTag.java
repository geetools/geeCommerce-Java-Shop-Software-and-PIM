package com.geecommerce.core.web.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.geecommerce.core.App;

public class TranslateTag extends SimpleTagSupport {
    private String var = null;
    private String text = null;
    private Object p1 = null;
    private Object p2 = null;
    private Object p3 = null;

    @Override
    public void doTag() throws JspException, IOException {
        String translatedText = App.get().message(text);

        if (translatedText != null) {
            List<Object> params = new ArrayList<>();
            if (p1 != null)
                params.add(p1);
            if (p2 != null)
                params.add(p2);
            if (p3 != null)
                params.add(p3);

            if (params.size() > 0) {
                translatedText = String.format(translatedText, params.toArray());
            }

            if (var != null) {
                getJspContext().setAttribute(var, translatedText);
            } else {
                JspWriter out = getJspContext().getOut();
                out.print(translatedText);
            }
        }
    }

    public void setVar(String var) {
        this.var = var;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setParam1(Object param1) {
        this.p1 = param1;
    }

    public void setParam2(Object param2) {
        this.p2 = param2;
    }

    public void setParam3(Object param3) {
        this.p3 = param3;
    }
}
