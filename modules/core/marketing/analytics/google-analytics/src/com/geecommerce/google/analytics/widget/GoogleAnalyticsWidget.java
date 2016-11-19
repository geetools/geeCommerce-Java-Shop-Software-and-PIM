package com.geecommerce.google.analytics.widget;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;

@Widget(name = "google_analytics")
public class GoogleAnalyticsWidget extends AbstractWidgetController implements WidgetController {
    private static final String CONF_KEY_GOOGLE_ANALYTICS_ACCOUNT_ID = "google/analytics/account_id";
    private static final String CONF_KEY_GOOGLE_ANALYTICS_DOMAIN = "google/analytics/domain";
    private static final String PARAM_ACCOUNT_ID = "accountId";
    private static final String PARAM_DOMAIN = "domain";
    private static final String VIEW = "google/analytics/view";

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws Exception {
        String accountId = app.cpStr_(CONF_KEY_GOOGLE_ANALYTICS_ACCOUNT_ID);
        String domain = app.cpStr_(CONF_KEY_GOOGLE_ANALYTICS_DOMAIN);

        if (accountId != null && !accountId.trim().isEmpty() && domain != null && !domain.trim().isEmpty()) {
            widgetCtx.setParam(PARAM_ACCOUNT_ID, accountId);
            widgetCtx.setParam(PARAM_DOMAIN, domain);

            widgetCtx.render(VIEW);
        }
    }
}
