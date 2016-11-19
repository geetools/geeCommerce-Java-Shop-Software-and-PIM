package com.geecommerce.news.subscription.widget;

import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.geecommerce.core.system.widget.repository.WidgetParameters;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.google.inject.Inject;

@Widget(name = "cp_news_subsсriber", cms = false)
public class NewsSubscriberWidget extends AbstractWidgetController implements WidgetController {

    private final WidgetParameters widgetParameters;

    private final String PARAM_TEMPLATE = "template";
    private final String DEFAULT_TEMPLATE = "newsletter";

    @Inject
    public NewsSubscriberWidget(WidgetParameters widgetParameters) {
        this.widgetParameters = widgetParameters;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws Exception {

        String template = widgetCtx.getParam(PARAM_TEMPLATE);

        widgetCtx.setParam("wd_guid", UUID.randomUUID().toString());

        if (StringUtils.isBlank(template)) {
            widgetCtx.render("home/" + DEFAULT_TEMPLATE);
        } else {
            widgetCtx.render("home/" + template);
        }
    }

}