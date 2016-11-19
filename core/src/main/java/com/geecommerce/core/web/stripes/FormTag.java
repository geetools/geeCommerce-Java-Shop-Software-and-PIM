package com.geecommerce.core.web.stripes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.core.App;

import net.sourceforge.stripes.exception.StripesJspException;
import net.sourceforge.stripes.util.UrlBuilder;

public class FormTag extends net.sourceforge.stripes.tag.FormTag {
    private boolean ssl = false;
    private Object beanclass;

    public void setBeanclass(Object beanclass) throws StripesJspException {
        this.beanclass = beanclass;

        super.setBeanclass(beanclass);
    }

    protected String buildAction() {
        String action = new UrlBuilder(pageContext.getRequest().getLocale(), getAction(), false).setEvent(null).toString();

        if (action.startsWith("/")) {
            HttpServletRequest request = (HttpServletRequest) getPageContext().getRequest();
            String contextPath = request.getContextPath();

            // *Always* prepend the context path if "beanclass" was used
            // Otherwise, *only* prepend it if it is not already present
            if (contextPath.length() > 1 && (beanclass != null || !action.startsWith(contextPath + '/'))) {
                action = contextPath + action;
            }
        }

        if (isSsl()) {
            action = new StringBuilder(App.get().getSecureBasePath()).append(action).toString();
        }

        HttpServletResponse response = (HttpServletResponse) getPageContext().getResponse();
        return response.encodeURL(action);
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }
}