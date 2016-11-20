package com.geecommerce.core.web.api;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.core.system.widget.model.WidgetParameter;
import com.geecommerce.core.system.widget.model.WidgetParameterOption;
import com.geecommerce.core.type.Id;

public interface WidgetController {
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response,
        ServletContext servletContext) throws Exception;

    public String getCode();

    public String getView(WidgetContext ctx);

    public String getDefaultView();

    public List<WidgetParameter> getParameters();

    public List<WidgetParameterOption> getParameterOptions(Id parameterId);

    public boolean isEnabled();

    public boolean isCmsEnabled();

    public boolean isJavascriptEnabled();

    public boolean isCssEnabled();
}
