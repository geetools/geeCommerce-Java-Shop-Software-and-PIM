package com.geecommerce.google.analytics.widget;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;

@Widget(name = "google_tag_manager")
public class GoogleTagManagerWidget extends AbstractWidgetController implements WidgetController {

    private static final String CONF_KEY_GOOGLE_TAG_MANAGER_ACCOUNT_ID = "google/gtm/account_id";
    private static final String VIEW = "google/tag_manager/view";
    private static final String PARAM_ACCOUNT_ID = "gtmAccountId";

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws Exception {
        String accountId = app.cpStr_(CONF_KEY_GOOGLE_TAG_MANAGER_ACCOUNT_ID);
        if (accountId != null && !accountId.isEmpty()) {
            widgetCtx.setParam(PARAM_ACCOUNT_ID, accountId);
            widgetCtx.render(VIEW);
        }
    }
}
