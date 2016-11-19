package com.geecommerce.core.app.standalone.helper;

import com.geecommerce.core.App;
import com.geecommerce.core.system.attribute.service.AttributeService;
import com.geecommerce.core.system.service.SystemService;

public class ServiceHelper {
    public static final AttributeService getAttributeService() {
        return App.get().getService(AttributeService.class);
    }

    public static final SystemService getSystemService() {
        return App.get().getService(SystemService.class);
    }
}
