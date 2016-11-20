package com.geecommerce.core.system.helper;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.service.TargetSupport;
import com.geecommerce.core.service.annotation.Helper;
import com.geecommerce.core.type.ContextObject;
import com.google.inject.Inject;

@Helper
public class DefaultTargetSupportHelper implements TargetSupportHelper {
    @Inject
    protected App app;

    @Override
    public String findURI(TargetSupport targetSupport) {
        ContextObject<String> targetURI = targetSupport.getURI();

        ApplicationContext appCtx = app.context();

        String uri = targetURI.getStr(appCtx.getLanguage());

        if (uri == null)
            uri = (String) targetURI.getGlobalValue();

        return uri;
    }
}
